//
//  SettingsViewController.swift
//  SmartMeteriOS
//
//  Created by Tobias Fischer on 09.11.21.
//

import UIKit

final class SettingsViewController: UITableViewController {
    @IBOutlet weak var lblAddress: UITextField! // IP Address
    @IBOutlet weak var lblKey: UITextField! // AES Key
    
    private let spinnerViewController = SpinnerViewController() // loading spinner
    private let userDefaults = UserDefaults.standard
    
    private var isIPSuccess = true { // correct IP Address input
        didSet {
            self.lblAddress.textColor = self.isIPSuccess ? .label : .systemRed
        }
    }
    
    private var isAESKeySuccess = true { // correct AES Key input
        didSet {
            self.lblKey.textColor = self.isAESKeySuccess ? .label : .systemRed
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        lblAddress.text = userDefaults.string(forKey: Constants.defaultsIp)
        lblKey.text = userDefaults.string(forKey: Constants.defaultsAES)
        checkIPAddress()
        checkAESKey()
        hideKeyboardWhenTappedAround()
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
    }
    
    /**
     button check settings clicked
     */
    @IBAction func btnCheckClicked(_ sender: UIButton) {
        checkIPAddress()
        checkAESKey()
    }
    
    /**
     IP Address changed
     */
    @IBAction func txtAddressEdited(_ sender: UITextField) {
        checkIPAddress()
    }
    
    /**
     check IP Address input
     */
    private func checkIPAddress() {
        if let text = lblAddress.text {
            self.userDefaults.set(text, forKey: Constants.defaultsIp)
            
            let range = NSRange(location: 0, length: text.utf16.count)
            // IP Address RegEx
            let regex = try! NSRegularExpression(pattern: "^(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))\\.(\\d|[1-9]\\d|1\\d\\d|2([0-4]\\d|5[0-5]))$")
            
            if regex.firstMatch(in: text, options: [], range: range) == nil {
                // wrong user input (based on RegEx)
                isIPSuccess = false
                self.checkInput()
            } else {
                createSpinnerView()
                // check connection to address
                NetworkUtil.checkConnection(url: text){(error: Error?) in
                    DispatchQueue.main.async {
                        if(error == nil){
                            // right address
                            self.isIPSuccess = true
                        }
                        else {
                            // wrong address
                            self.isIPSuccess = false
                        }
                        self.removeSpinnerView()
                        self.checkInput()
                    }
                }
            }
        }
    }
    
    /**
     creates loading spinner
     */
    private func createSpinnerView() {
        // add the spinner view controller
        addChild(spinnerViewController)
        spinnerViewController.view.frame = view.frame
        view.addSubview(spinnerViewController.view)
        spinnerViewController.didMove(toParent: self)
    }
    
    /**
     removes loading spinner
     */
    private func removeSpinnerView() {
        spinnerViewController.willMove(toParent: nil)
        spinnerViewController.view.removeFromSuperview()
        spinnerViewController.removeFromParent()
    }
    
    /**
     AES Key Changed
     */
    @IBAction func txtKeyEdited(_ sender: UITextField) {
        checkAESKey()
        checkInput()
    }
    
    /**
     checks AES key
     */
    private func checkAESKey() {
        if let text = lblKey.text {
            userDefaults.set(text, forKey: Constants.defaultsAES)
            
            let range = NSRange(location: 0, length: text.utf16.count)
            // RegEx for an AES Key
            let regex = try! NSRegularExpression(pattern: "^([0-9A-F]{2} ){15}[0-9A-F]{2}$")
            
            if regex.firstMatch(in: text, options: [], range: range) == nil {
                // wrong user input (based on RegEx)
                isAESKeySuccess = false
            } else {
                isAESKeySuccess = true
            }
            checkInput()
        }
    }
    
    /**
     checks if input is correct (based on that - set UserDefaults correctness flag)
     */
    private func checkInput() {
        if isAESKeySuccess && isIPSuccess {
            userDefaults.set(true, forKey: Constants.defaultsCorrect)
        }
        else {
            userDefaults.set(false, forKey: Constants.defaultsCorrect)
        }
    }
}

/**
 hide keyboard when not tapped on keyboard (otherwise user can't close keyboard)
 */
extension SettingsViewController {
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(SettingsViewController.dismissKeyboard))
        view.addGestureRecognizer(tap)

    }

    @objc func dismissKeyboard() {
        view.endEditing(true)
    }
}
