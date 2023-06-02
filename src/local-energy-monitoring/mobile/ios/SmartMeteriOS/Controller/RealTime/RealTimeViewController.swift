//
//  RealTimeViewController.swift
//  SmartMeteriOS
//
//  Created by Tobias Fischer on 09.11.21.
//

import UIKit

final class RealTimeViewController: UIViewController {
    
    @IBOutlet weak var lblPower: UILabel!
    @IBOutlet weak var lblPowerUnit: UILabel!
    @IBOutlet weak var lblTime: UILabel!
    @IBOutlet weak var lblTara: UILabel!
    @IBOutlet weak var lblTaraConstraint: NSLayoutConstraint!
    @IBOutlet weak var imgStatus: UIImageView!
    @IBOutlet weak var tblRealTime: UITableView!
    
    private let userDefaults = UserDefaults.standard
    private var urlString: String = ""
    private var aesKey: String = ""
    
    private var signalRService: SignalRService<MeterDataRTDto>?
    private var isStarted = false // is SignalR currently started
    private var isCreated = false // is a SignalRService currently created
    
    private var meterDataList: [MeterDataRTDto] = [] // list of MeterData value for TableView
    private var colors: [UIColor] = [] // list of colors for TableView
    
    private var previousMeterData: MeterDataDto<Double>? // previous MeterData value for comparing
    private var taraValues: MeterDataDto<Double>? // tara value
    
    // MARK: - Lifecycle Methods
    override func viewDidLoad() {
        super.viewDidLoad()
        print("SmartMeteriOS::RealTime::viewDidLoad()")
        
        tblRealTime.dataSource = self
        tblRealTime.delegate = self
        
        createSignalRService()
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        print("SmartMeteriOS::RealTime::viewWillAppear()")
        if isCreated {startSignalRService()}
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        print("SmartMeteriOS::RealTime::viewWillDisappear()")
        stopSignalRService()
    }
    
    // MARK: - Helper
    /**
     returns appropriate meter data value (for example activePowerPlus)
     if you want to present another value --> to be changed in only one method
     */
    private func getAppropriateMeterDataValues<T>(_ meterDataDto: MeterDataDto<T>) -> T {
        return meterDataDto.activePowerPlus
    }
    
    /**
     returns MeterData with changed meter data value (for example activePowerPlus)
     */
    private func setAppropriateMeterDataValues<T>(_ meterDataDto: MeterDataDto<T>, value: T) -> MeterDataDto<T> {
        var copiedMeterDataDto = meterDataDto
        copiedMeterDataDto.activePowerPlus = value
        return copiedMeterDataDto
    }
    
    /**
     returns url of raspberry stored in user defaults
     */
    private func getUrlString() -> String {
        if urlString == "" {
            if let url = userDefaults.string(forKey: Constants.defaultsIp) {
                urlString = url
            }
        }
        return urlString
    }
    
    /**
     returns aes key stroed in user defaults
     */
    private func getAesKey() -> String {
        if aesKey == "" {
            if let key = userDefaults.string(forKey: Constants.defaultsAES) {
                aesKey = "AES\(key.replacingOccurrences(of: " ", with: ""))END"
            }
        }
        return aesKey
    }
    
    // MARK: - Button Events
    /**
     set new tara value (like tara on scales)
     */
    @IBAction func taraClicked(_ sender: UIButton) {
        if(meterDataList.count > 0) {
            taraValues = previousMeterData
            
            if let taraValues = taraValues {
                let taraText = "Tara \(String(format: "%.0f", getAppropriateMeterDataValues(taraValues))) \(getAppropriateMeterDataValues(meterDataList[0].unit))"
                
                // animate growing constraint and label
                UIView.animate(withDuration: 0.5) {
                    self.lblTaraConstraint.constant = 16
                    self.lblTara.text = taraText
                    self.view.layoutIfNeeded()
                }
                
                clearHist()
            }
        }
    }
    
    /**
     reset tara value and delete whole table
     */
    @IBAction func resetClicked(_ sender: UIButton) {
        taraValues = nil
        lblTara.text = ""
        clearHist()
        
        // animate shrinking constraint and label
        UIView.animate(withDuration: 0.5) {
            self.lblTaraConstraint.constant = 0
            self.lblTara.text = ""
            self.view.layoutIfNeeded()
        }
    }
    
    /**
     clears entire screen
     */
    private func clearHist() {
        colors.removeAll()
        meterDataList.removeAll()
        tblRealTime.reloadData()
        imgStatus.tintColor = .systemBlue
        lblPower.text = "0"
        lblPower.textColor = .systemBlue
        lblPowerUnit.textColor = .systemBlue
        imgStatus.image = UIImage.init(systemName: "minus")
    }
    
    // MARK: - SignalR
    /**
     create SignalR listener
     */
    private func createSignalRService() {
        // check connection to raspberry with a GET request
        NetworkUtil.checkConnection(url: getUrlString()){(error: Error?) in
            if(error == nil){
                guard let url = URL(string: "http://\(self.getUrlString()):8080/smartmeter/realtime") else {
                    self.handleError(error: NetworkError.couldNotParseUrl)
                    return
                }
                self.signalRService = SignalRService<MeterDataRTDto>(url: url, method: self.getAesKey(), handleMessage: self.handleMeterData)
                self.isCreated = true
                self.startSignalRService()
            }
            else {
                self.handleError(error: error!)
                return
            }
        }
    }
    
    /**
     start SignalR listener
     */
    private func startSignalRService() {
        if !isStarted {
            if let signalRService = signalRService {
                signalRService.start()
                isStarted = true
                checkSignalRConnection()
            } else {
                handleError(error: NetworkError.signalRConnectionFailed)
            }
        }
    }
    
    /**
     checks whether data has arrived 3 seconds after the listener started
     */
    private func checkSignalRConnection() {
        var timestampBefore5Sec = ""
        if let previousMeterData = previousMeterData {
            timestampBefore5Sec = previousMeterData.timestamp
        }
        // wait 3 seconds
        DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
            if(self.meterDataList.count == 0 || timestampBefore5Sec == self.meterDataList[0].meterDataValues.timestamp ){
                self.handleError(error: NetworkError.signalRConnectionFailed)
            }
        }
    }
    
    /**
     stop SignalR listener
     */
    private func stopSignalRService() {
        if let signalRService = signalRService {
            signalRService.stop()
            isStarted = false
        }
    }
    
    // MARK: - Handle Data
    /**
     new MeterData arrived
     */
    private func handleMeterData(meterDataRTDto: MeterDataRTDto) {
        var meterDataValue = getAppropriateMeterDataValues(meterDataRTDto.meterDataValues)
        
        // substract tara (if set)
        if let taraValues = taraValues {
            meterDataValue -= getAppropriateMeterDataValues(taraValues)
        }
        
        lblPower.text = String(format: "%.0f", meterDataValue)
        lblPowerUnit.text = getAppropriateMeterDataValues(meterDataRTDto.unit)
        lblTime.text = String(meterDataRTDto.meterDataValues.timestamp.split(separator: "T")[1])
        
        // insert to list for TableView
        var meterDataRTDto4List = meterDataRTDto
        meterDataRTDto4List.meterDataValues = setAppropriateMeterDataValues(meterDataRTDto.meterDataValues, value: meterDataValue)
        meterDataList.insert(meterDataRTDto4List, at: 0)
        
        // compare with previous MeterData
        if let previousMeterData = previousMeterData {
            meterDataValue = getAppropriateMeterDataValues(meterDataRTDto.meterDataValues)
            let previousMeterDataValue = getAppropriateMeterDataValues(previousMeterData)
            
            if meterDataValue < previousMeterDataValue {compareMeterValues(.lower)}
            else if meterDataValue > previousMeterDataValue {compareMeterValues(.greater)}
            else {compareMeterValues(.equal)}
        } else {
            // initial setting is greater
            compareMeterValues(.greater)
        }
        
        previousMeterData = meterDataRTDto.meterDataValues
        tblRealTime.reloadData()
    }
    
    /**
     sets status based on comparison with previous value (color and image)
     */
    private func compareMeterValues(_ compareOperator: CompareOperator) {
        var color = UIColor.systemBlue
        
        switch compareOperator {
        case .lower:
            color = .systemGreen
            imgStatus.image = UIImage.init(systemName: "arrow.down")
        case .equal:
            color = .systemBlue
            imgStatus.image = UIImage.init(systemName: "minus")
        case .greater:
            color = .systemRed
            imgStatus.image = UIImage.init(systemName: "arrow.up")
        }
        
        colors.insert(color, at: 0)
        
        imgStatus.tintColor = color
        lblPower.textColor = color
        lblPowerUnit.textColor = color
    }
    
    /**
     handels several error message and displays a alert (with "Try Again" button --> creates new SignalRService)
     */
    private func handleError(error: Error) {
        print("SmartMeteriOS::RealTime::Error: \(error)")
        if !(error is UIError){
            DispatchQueue.main.async {
                let alert = UIAlertController(title: "Error", message: error.localizedDescription, preferredStyle: .actionSheet)
                
                alert.addAction(UIAlertAction(title: NSLocalizedString("Try Again", comment: "Trying again"), style: .default, handler: {_ in
                    self.isStarted = false
                    self.createSignalRService()
                }))
                
                self.present(alert, animated: true, completion: nil)
            }
        }
    }
    
    
}

extension RealTimeViewController: UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return meterDataList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if(meterDataList.count > 0) {
            guard let cell = tableView.dequeueReusableCell(withIdentifier: RealTimeTableViewCell.cellId, for: indexPath) as? RealTimeTableViewCell else {
                handleError(error: UIError.couldNotCreateCell)
                return UITableViewCell();
            }
            
            let meterData = meterDataList[indexPath.row]
            cell.lblTime.text = String(meterData.meterDataValues.timestamp.split(separator: "T")[1])
            cell.lblPower.text = String(format: "%.0f", getAppropriateMeterDataValues(meterData.meterDataValues))
            cell.lblPowerUnit.text = getAppropriateMeterDataValues(meterData.unit)
            cell.color = colors[indexPath.row]
            
            return cell
        }
        return UITableViewCell()
    }
}

extension RealTimeViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
}

