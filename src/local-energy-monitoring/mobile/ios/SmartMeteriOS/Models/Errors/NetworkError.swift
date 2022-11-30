//
//  NetworkError.swift
//  SmartMeteriOS
//
//  Created by Tobias Fischer on 17.11.21.
//

import Foundation

enum NetworkError: Error {
    case couldNotParseUrl
    case noResponseData
    case invalidUTF8
    case signalRConnectionFailed
}

extension NetworkError: LocalizedError {
    public var errorDescription: String? {
        switch self {
        case .couldNotParseUrl: return NSLocalizedString("Could not translate address which refers to Smart Meter.", comment: "url parsing")
        case .noResponseData: return NSLocalizedString("No response from Smart Meter.", comment: "no response")
        case .invalidUTF8: return NSLocalizedString("Received wrong format from Smart Meter.", comment: "invalid UTF8")
        case .signalRConnectionFailed: return NSLocalizedString("Real Time connection to Smart Meter failed.", comment: "SignalR error")
        }
    }
}
