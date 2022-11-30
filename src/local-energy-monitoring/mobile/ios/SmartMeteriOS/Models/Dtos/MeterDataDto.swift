//
//  MeterDataDto.swift
//  SmartMeteriOS
//
//  Created by Tobias Fischer on 14.11.21.
//

import Foundation

struct MeterDataDto<T: Decodable> {
    var id: Int
    var timestamp: String
    var activeEnergyPlus: T
    var activeEnergyMinus: T
    var reactiveEnergyPlus: T
    var reactiveEnergyMinus: T
    var activePowerPlus: T
    var activePowerMinus: T
    var reactivePowerPlus: T
    var reactivePowerMinus: T
    
    init(id: Int, timestamp: String, activeEnergyPlus: T, activeEnergyMinus: T,reactiveEnergyPlus: T, reactiveEnergyMinus: T, activePowerPlus: T, activePowerMinus: T, reactivePowerPlus: T, reactivePowerMinus: T){
        self.id = id
        self.timestamp = timestamp
        self.activeEnergyPlus = activeEnergyPlus
        self.activeEnergyMinus = activeEnergyMinus
        self.reactiveEnergyPlus = reactiveEnergyPlus
        self.reactiveEnergyMinus = reactiveEnergyMinus
        self.activePowerPlus = activePowerPlus
        self.activePowerMinus = activePowerMinus
        self.reactivePowerPlus = reactivePowerPlus
        self.reactivePowerMinus = reactivePowerMinus
    }
}

extension MeterDataDto: Decodable {
    enum CodingKeys: String, CodingKey {
        case id
        case timestamp
        case activeEnergyPlus
        case activeEnergyMinus
        case reactiveEnergyPlus
        case reactiveEnergyMinus
        case activePowerPlus
        case activePowerMinus
        case reactivePowerPlus
        case reactivePowerMinus
    }
    
    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        let id = try container.decode(Int.self, forKey: .id)
        let timestamp = try container.decode(String.self, forKey: .timestamp)
        let activeEnergyPlus = try container.decode(T.self, forKey: .activeEnergyPlus)
        let activeEnergyMinus = try container.decode(T.self, forKey: .activeEnergyMinus)
        let reactiveEnergyPlus = try container.decode(T.self, forKey: .reactiveEnergyPlus)
        let reactiveEnergyMinus = try container.decode(T.self, forKey: .reactiveEnergyMinus)
        let activePowerPlus = try container.decode(T.self, forKey: .activePowerPlus)
        let activePowerMinus = try container.decode(T.self, forKey: .activePowerMinus)
        let reactivePowerPlus = try container.decode(T.self, forKey: .reactivePowerPlus)
        let reactivePowerMinus = try container.decode(T.self, forKey: .reactivePowerMinus)
        
        self.init(id: id, timestamp: timestamp, activeEnergyPlus: activeEnergyPlus, activeEnergyMinus: activeEnergyMinus, reactiveEnergyPlus: reactiveEnergyPlus, reactiveEnergyMinus: reactiveEnergyMinus, activePowerPlus: activePowerPlus, activePowerMinus: activePowerMinus, reactivePowerPlus: reactivePowerPlus, reactivePowerMinus: reactivePowerMinus)
    }
}
