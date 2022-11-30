//
//  MeterDataHistDto.swift
//  SmartMeteriOS
//
//  Created by Michael Zauner on 20.11.21.
//

import Foundation
import UIKit

//data class MeterDataHistDto (var Unit: MeterDataDto<String>, var Min: MeterDataDto<Double>, var Avg: MeterDataDto<Double>, var Max: MeterDataDto<Double>, var MeterDataValues: ArrayList<MeterDataDto<Double>>)

struct MeterDataHistDto {
    var unit : MeterDataDto<String>
    var min : MeterDataDto<Double>
    var avg : MeterDataDto<Double>
    var max: MeterDataDto<Double>
    
    init(unit: MeterDataDto<String>, min: MeterDataDto<Double>, avg: MeterDataDto<Double>, max: MeterDataDto<Double>) {
        self.unit = unit
        self.min = min
        self.avg = avg
        self.max = max
    }
}

extension MeterDataHistDto: Decodable {
    enum CodingKeys: String, CodingKey {
        case unit
        case min
        case avg
        case max
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let unit = try container.decode(MeterDataDto<String>.self, forKey: .unit)
        let min = try container.decode(MeterDataDto<Double>.self, forKey: .min)
        let avg = try container.decode(MeterDataDto<Double>.self, forKey: .avg)
        let max = try container.decode(MeterDataDto<Double>.self, forKey: .max)
        
        self.init(unit: unit, min: min, avg: avg, max: max)
    }
}
