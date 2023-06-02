//
//  SpinnerViewController.swift
//  SmartMeteriOS
//
//  Created by Tobias Fischer on 20.11.21.
//

import UIKit

final class SpinnerViewController: UIViewController {
    
    var spinner = UIActivityIndicatorView(style: .large)
    
    override func loadView() {
        view = UIView()
        
        spinner.translatesAutoresizingMaskIntoConstraints = false
        spinner.startAnimating()
        view.addSubview(spinner)
        
        spinner.centerXAnchor.constraint(equalTo: view.centerXAnchor).isActive = true
        spinner.centerYAnchor.constraint(equalTo: view.centerYAnchor).isActive = true
    }
}
