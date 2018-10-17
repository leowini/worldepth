//
//  PostEnvironment.swift
//  Worldepth
//
//  Created by Soren Dahl on 10/10/18.
//  Copyright © 2018 Soren Dahl. All rights reserved.
//

import UIKit
import SceneKit


class PostEnvironment {
    
    //MARK: Properties
    
    //placeholder until author object is made
    var author: String
    var title: String
    var model: SCNScene?
    var thumbnail: UIImage?
    var rating: Float
    
    
    //MARK: Initialization
    
    init?(author: String, title: String, model: SCNScene?, thumbnail: UIImage?, rating: Float){
        
        //tests whether it is possible
        guard !author.isEmpty else{
            return nil
        }
        
        guard !title.isEmpty else {
            return nil
        }
        
        guard !(rating < 0 || rating >= 5) else {
            return nil
        }
        
        self.author = author
        self.title = title
        self.model = model
        self.thumbnail = thumbnail
        self.rating = rating
    }
    
    
}
