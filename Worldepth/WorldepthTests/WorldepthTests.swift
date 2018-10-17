//
//  WorldepthTests.swift
//  WorldepthTests
//
//  Created by Soren Dahl on 10/10/18.
//  Copyright © 2018 Soren Dahl. All rights reserved.
//

import XCTest
@testable import Worldepth

class WorldepthTests: XCTestCase {

    override func setUp() {
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
    }

    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measure {
            // Put the code you want to measure the time of here.
        }
    }
    
    func testInitSucceeds() {
        
        let fiveRating = PostEnvironment(author: "Soren", title: "TestPost", model: nil, thumbnail: nil, rating: 5)
        XCTAssertNil(fiveRating)
        
        let zeroRating = PostEnvironment(author: "Soren", title: "TestPost", model: nil, thumbnail: nil, rating: 0)
        XCTAssertNil(zeroRating)
        
    }
    
    func testInitFails() {
        
        let negative = PostEnvironment(author: "Soren", title: "TestPost", model: nil, thumbnail: nil, rating: -1)
        XCTAssertNil(negative)
        
        let noName = PostEnvironment(author: "", title: "TestPost", model: nil, thumbnail: nil, rating: 5)
        XCTAssertNil(noName)
        
        let noTitle = PostEnvironment(author: "Soren", title: "", model: nil, thumbnail: nil, rating: 5)
        XCTAssertNil(noTitle)
        
        let largeRating = PostEnvironment(author: "Soren", title: "TestPost", model: nil, thumbnail: nil, rating: 6)
        XCTAssertNil(largeRating)
        
        
    }

}
