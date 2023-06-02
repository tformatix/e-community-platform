//
//  RealTimeTableViewCell.swift
//  SmartMeteriOS
//
//  Created by Tobias Fischer on 17.11.21.
//

import UIKit

final class RealTimeTableViewCell: UITableViewCell {
    static let cellId = "realtimeCellIdentifier" // cell identifier
    @IBOutlet weak var lblTime: UILabel!
    @IBOutlet weak var lblPower: UILabel!
    @IBOutlet weak var lblPowerUnit: UILabel!
    
    var color: UIColor? {
        didSet {
            lblTime.textColor = color
            lblPower.textColor = color
            lblPowerUnit.textColor = color
        }
    }
}
