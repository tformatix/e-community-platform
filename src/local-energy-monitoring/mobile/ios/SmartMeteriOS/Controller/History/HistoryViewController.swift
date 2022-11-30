//
//  HistoryViewController.swift
//  SmartMeteriOS
//
//  Created by Tobias Fischer on 09.11.21.
//

import UIKit

final class HistoryViewController: UIViewController {
    
    private var historySetupDto : HistorySetupDto?
    private var meterDataHistDto: MeterDataHistDto?
    private let spinnerViewController = SpinnerViewController()
    private var historyValues : [Double] = []
    
    @IBOutlet weak var btnRefresh: UIButton!
    @IBOutlet weak var tableViewHistory: UITableView!
    @IBOutlet weak var fromTimestamp: UIDatePicker!
    @IBOutlet weak var toTimestamp: UIDatePicker!
    @IBOutlet weak var sliderTimeResolution: UISlider!
    @IBOutlet weak var labelTimeResolution: UILabel!
    
    private var timeResolutions = [
        15: "15min",
        30: "30min",
        60: "60min",
        120: "2h",
        300: "5h",
        720: "12h",
        1440: "1T",
        43800: "1M",
        525600: "1Y"].sorted{ (first, second) -> Bool in
            return first.key < second.key
        }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        createSpinnerView()
        
        // HistorySetup Request
        requestHistorySetup(){(error: Error?) in
            if(error == nil){
                DispatchQueue.main.async {
                    self.setupHistoryView()
                    self.removeSpinnerView()
                }
            }
            else {
                self.handleError(error: error!)
                return
            }
        }
        
        tableViewHistory.delegate = self
        tableViewHistory.dataSource = self
    }
    
    /**
     create loading spinner
     */
    private func createSpinnerView() {
        // add the spinner view controller
        addChild(spinnerViewController)
        spinnerViewController.view.frame = view.frame
        view.addSubview(spinnerViewController.view)
        spinnerViewController.didMove(toParent: self)
    }
    
    /**
     remove loading spinner
     */
    private func removeSpinnerView() {
        spinnerViewController.willMove(toParent: nil)
        spinnerViewController.view.removeFromSuperview()
        spinnerViewController.removeFromParent()
    }
    
    /**
     set dates from History Request
     */
    private func setupHistoryView() {
        let dateFormatter = DateFormatter()
        
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        
        if let histSetup = historySetupDto {
            if let date = dateFormatter.date(from: histSetup.initTimestamp) {
                fromTimestamp.date = date
                fromTimestamp.minimumDate = date
                toTimestamp.minimumDate = date
                toTimestamp.maximumDate = Date.now
            }
            
            setMaxTimeResolution(max: histSetup.maxTimeResolution)
        }
    }
    
    /**
     set max Timeresolution
     */
    private func setMaxTimeResolution(max: Int) {
        let maxIndex = timeResolutions.filter{ $0.key <= max }.count-1
        
        sliderTimeResolution.maximumValue = Float(maxIndex)
        sliderTimeResolution.setValue(0, animated: true)
    }
    
    /**
     send HistorySetup Request; used for min date and timeresolution
     */
    private func requestHistorySetup(connected: @escaping (Error?) -> Void) {
        guard let url = URL(string: "http://\(getIp()):8080/smartmeter/historysetup/\(getAesKey())") else {
            connected(NetworkError.couldNotParseUrl)
            return
        }
        
        print(url.absoluteString)
        let task = URLSession.shared.dataTask(with: url) { data, response, error in
            if error != nil {
                connected(error)
                return
            }
            
            guard let responseData = data else {
                connected(NetworkError.couldNotParseUrl)
                return
            }
            
            if let _ = String(bytes: responseData, encoding: .utf8) {
                let decoder = JSONDecoder()
                do {
                    self.historySetupDto = try decoder.decode(HistorySetupDto.self, from: responseData)
                    connected(nil)
                } catch {
                    connected(error)
                }
                return
            } else {
                connected(NetworkError.invalidUTF8)
                return
            }
        }
        task.resume()
    }
    
    private func handleError(error: Error) {
        print(error.localizedDescription)
    }
    
    /**
     update timeResolution value when slider value changed
     */
    @IBAction func onValueChanged(_ sender: UISlider) {
        let selectedValue = Int(sender.value)
        
        for (index, element) in timeResolutions.enumerated() {
            if (index == selectedValue) {
                self.labelTimeResolution.text = element.value
            }
        }
    }
    
    /**
     send HistoryRequest
     */
    @IBAction func onRefreshButtonClicked(_ sender: UIButton) {
        createSpinnerView()
        let dateFormatter = DateFormatter()
        
        dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"
        
        let toTimestampString = dateFormatter.string(from: toTimestamp.date)
        let fromTimestampString = dateFormatter.string(from: fromTimestamp.date)
        let aes: String = getAesKey()
        
        // clear HistoryValues
        historyValues = []
        var timeResolution = 15
        
        for (index, element) in timeResolutions.enumerated() {
            if (index == Int(sliderTimeResolution.value)) {
                timeResolution = element.key
            }
        }
        
        let historyDto = HistoryDto(aesKey: aes, fromTimestamp: fromTimestampString, toTimestamp: toTimestampString, timeResolution: timeResolution)
        
        guard let url = URL(string: "http://\(getIp()):8080/smartmeter/history") else {
            return
        }
        
        let encoder = JSONEncoder()
        encoder.outputFormatting = .prettyPrinted
        let data = try! encoder.encode(historyDto)
        
        sendHistoryRequest(to: url, body: data) { result in
            if let min = self.meterDataHistDto?.min.activeEnergyPlus {
                self.historyValues.append(min)
            }
            if let avg = self.meterDataHistDto?.avg.activeEnergyPlus {
                self.historyValues.append(avg)
            }
            if let max = self.meterDataHistDto?.max.activeEnergyPlus {
                self.historyValues.append(max)
            }
            
            DispatchQueue.main.async {
                self.tableViewHistory.reloadData()
                self.removeSpinnerView()
            }
        }
    }
    
    func sendHistoryRequest(to url: URL, body: Data, then handler: @escaping(_ response: Data) -> Void) {
        
        var request = URLRequest(url: url, cachePolicy: .reloadIgnoringLocalCacheData)
        
        request.httpMethod = "POST"
        request.httpBody = body
        request.setValue("application/json", forHTTPHeaderField: "Accept")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        let task = URLSession.shared.dataTask(with: request, completionHandler: { data, response, error in
            
            if let responseData = data {
                if let _ = String(bytes: responseData, encoding: .utf8) {
                    let decoder = JSONDecoder()
                    do {
                        self.meterDataHistDto = try decoder.decode(MeterDataHistDto.self, from: responseData)
                        
                        handler(responseData)
                    } catch {
                        print(error.localizedDescription)
                    }
                } else {
                    return
                }
            }
        })
        
        task.resume()
    }
    
    private func getIp() -> String {
        if let url = UserDefaults.standard.string(forKey: Constants.defaultsIp) {
            return url
        }
        return ""
    }
    
    private func getAesKey() -> String {
        if let key = UserDefaults.standard.string(forKey: Constants.defaultsAES) {
            return "\(key.replacingOccurrences(of: " ", with: ""))"
        }
        return ""
    }
    
    /**
     check if from/to Dates are valid
     */
    @IBAction func onDateChanged(_ sender: UIDatePicker) {
        let toSeconds = Int64((toTimestamp.date.timeIntervalSince1970 * 1000.0).rounded())
        let fromSeconds = Int64((fromTimestamp.date.timeIntervalSince1970 * 1000.0).rounded())
        let diffInMin = ((toSeconds-fromSeconds) / 1000 / 60)
        
        if (fromSeconds > toSeconds) {
            self.btnRefresh.isEnabled = false
        }
        else if (diffInMin < 15) {
            self.btnRefresh.isEnabled = false
        }
        else {
            self.btnRefresh.isEnabled = true
        }
        
        // bug; reset slider and label
        sliderTimeResolution.value = 0
        labelTimeResolution.text = "15min"
        
        setMaxTimeResolution(max: Int(diffInMin))
    }
}

/**
 tableView methods
 */
extension HistoryViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return historyValues.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if(historyValues.count > 0) {
            guard let cell = tableView.dequeueReusableCell(withIdentifier: "historyCellIdentifier", for: indexPath) as? HistoryTableViewCell else {
                return UITableViewCell();
            }
            
            switch(indexPath.row) {
            case 0:
                cell.lblText.text = "Min:"
                cell.lblValue.textColor = UIColor.green
            case 1:
                cell.lblText.text = "Avg:"
                cell.lblValue.textColor = UIColor.blue
            case 2:
                cell.lblText.text = "Max:"
                cell.lblValue.textColor = UIColor.red
            default:
                cell.lblText.text = ""
            }
            
            let unit = self.meterDataHistDto?.unit.activeEnergyPlus ?? "Wh"
            
            cell.lblValue.text = "\(historyValues[indexPath.row]) \(unit)"
            
            return cell
        }
        return UITableViewCell()
    }
}

extension HistoryViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
}
