//
//  HistoryDto.swift
//  SmartMeteriOS
//
//  Created by Michael Zauner on 17.11.21.
//

import Foundation

struct HistoryDto: Codable {
    
    var aesKey: String
    var fromTimestamp: String
    var toTimestamp: String
    var timeResolution: Int
    
    
    init(aesKey: String, fromTimestamp: String, toTimestamp: String, timeResolution: Int) {
        self.aesKey = aesKey
        self.fromTimestamp = fromTimestamp
        self.toTimestamp = toTimestamp
        self.timeResolution = timeResolution
    }
}
