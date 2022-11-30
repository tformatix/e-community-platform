//
//  MeterDataRTDto.swift
//  SmartMeteriOS
//
//  Created by Tobias Fischer on 17.11.21.
//

import Foundation

struct MeterDataRTDto {
    var unit: MeterDataDto<String>
    var meterDataValues: MeterDataDto<Double>
    
    init(unit: MeterDataDto<String>, meterDataValues: MeterDataDto<Double>){
        self.unit = unit
        self.meterDataValues = meterDataValues
    }
}

extension MeterDataRTDto: Decodable {
    enum CodingKeys: String, CodingKey {
        case unit
        case meterDataValues
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let unit = try container.decode(MeterDataDto<String>.self, forKey: .unit)
        let meterDataValues = try container.decode(MeterDataDto<Double>.self, forKey: .meterDataValues)
        
        self.init(unit: unit, meterDataValues: meterDataValues)
    }
}
