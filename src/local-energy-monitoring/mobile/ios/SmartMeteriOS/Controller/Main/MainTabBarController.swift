//
//  MainTabBarController.swift
//  SmartMeteriOS
//
//  Created by Tobias Fischer on 20.11.21.
//

import UIKit

final class MainTabBarController: UITabBarController {
    
    private let userDefaults = UserDefaults.standard

    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.delegate = self

        if(userDefaults.bool(forKey: Constants.defaultsCorrect)) {
            // UserDefaults are set --> go to real time
            selectedIndex = 1
        } else {
            // UserDefaults not set --> go to settings
            selectedIndex = 2
        }
    }
}

extension MainTabBarController: UITabBarControllerDelegate {
    func tabBarController(_ tabBarController: UITabBarController, shouldSelect viewController: UIViewController) -> Bool {
        return userDefaults.bool(forKey: Constants.defaultsCorrect)
    }
}
