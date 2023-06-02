//
//  HistoryTableViewCell.swift
//  SmartMeteriOS
//
//  Created by Michael Zauner on 17.11.21.
//

import UIKit

final class HistoryTableViewCell: UITableViewCell {
    
    @IBOutlet weak var lblValue: UILabel!
    @IBOutlet weak var lblText: UILabel!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
