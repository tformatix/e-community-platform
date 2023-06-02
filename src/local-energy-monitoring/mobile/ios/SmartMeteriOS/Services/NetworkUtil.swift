//
//  NetworkUtil.swift
//  SmartMeteriOS
//
//  Created by Tobias Fischer on 20.11.21.
//

import Foundation

final class NetworkUtil {
    /**
     * check connectivity to raspberry
     */
    static func checkConnection(url: String, connected: @escaping (Error?) -> Void) {
        guard let url = URL(string: "http://\(url):8080/smartmeter/checkconnection") else {
            connected(NetworkError.couldNotParseUrl)
            return
        }
        
        let config = URLSessionConfiguration.default
        config.timeoutIntervalForResource = 3 // only wait 3 seconds for an answer
        
        let task = URLSession(configuration: config).dataTask(with: url) { data, response, error in
            if error != nil {
                connected(error)
                return
            }
            
            guard let responseData = data else {
                connected(NetworkError.noResponseData)
                return
            }
            
            if let _ = String(bytes: responseData, encoding: .utf8) {
                connected(nil)
                return
            } else {
                connected(NetworkError.invalidUTF8)
                return
            }
        }
        task.resume()
    }
}
