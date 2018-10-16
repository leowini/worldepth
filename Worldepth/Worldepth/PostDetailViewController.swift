//
//  PostDetailViewController.swift
//  Worldepth
//
//  Created by Soren Dahl on 10/12/18.
//  Copyright © 2018 Soren Dahl. All rights reserved.
//

import UIKit
import SceneKit

class PostDetailViewController: UIViewController {

    //MARK: Properties
    @IBOutlet weak var modelView: SCNView!

    var post: PostEnvironment?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        if let post = self.post{
            modelView.scene = post.model
            navigationItem.title = post.title
            modelView.autoenablesDefaultLighting = true
            modelView.allowsCameraControl = true
            
            
        }
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
