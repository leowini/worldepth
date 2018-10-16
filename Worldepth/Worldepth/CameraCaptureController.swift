//
//  CameraCaptureController.swift
//  Worldepth
//
//  Created by Soren Dahl on 10/14/18.
//  Copyright © 2018 Soren Dahl. All rights reserved.
//

import UIKit

class CameraCaptureController: UIViewController, FrameExtractorDelegate {
    
    func captured(image: UIImage) {
        cameraImage.image = image
    }
    

    var frameExtractor: FrameExtractor!
    @IBOutlet weak var cameraImage: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        frameExtractor = FrameExtractor()
        frameExtractor.delegate = self
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
