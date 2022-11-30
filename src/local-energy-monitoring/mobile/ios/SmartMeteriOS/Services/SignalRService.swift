//
//  SignalRService.swift
//  SmartMeteriOS
//
//  Created by Tobias Fischer on 09.11.21.
//

import Foundation
import SwiftSignalRClient

final class SignalRService<T:Decodable> {
    private var connection: HubConnection
    
    public init(url: URL, method: String, handleMessage: @escaping (T) -> Void)
    {
        connection = HubConnectionBuilder(url: url)
            .withLogging(minLogLevel: .error)
            .withAutoReconnect()
            .build()
        connection.on(method: method, callback: {(message: T) in
            handleMessage(message)
        })
    }
    
    func start() {
        connection.start()
    }
    
    func stop() {
        connection.stop()
    }
}
