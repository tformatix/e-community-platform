//
//  HistorySetupDto.swift
//  SmartMeteriOS
//
//  Created by Michael Zauner on 17.11.21.
//

import Foundation

struct HistorySetupDto {
    var latestTimestamp: String
    var maxTimeResolution: Int
    var initTimestamp: String
    
    init(latestTimestamp: String, maxTimeResolution: Int, initTimestamp: String) {
        self.latestTimestamp = latestTimestamp
        self.maxTimeResolution = maxTimeResolution
        self.initTimestamp = initTimestamp
    }
}

extension HistorySetupDto: Decodable {
    enum CodingKeys: String, CodingKey {
        case latestTimestamp
        case maxTimeResolution
        case initTimestamp
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let latestTimestamp = try container.decode(String.self, forKey: .latestTimestamp)
        let maxTimeresolution = try container.decode(Int.self, forKey: .maxTimeResolution)
        let initTimestamp = try container.decode(String.self, forKey: .initTimestamp)
        self.init(latestTimestamp: latestTimestamp, maxTimeResolution: maxTimeresolution, initTimestamp: initTimestamp)
    }
}
