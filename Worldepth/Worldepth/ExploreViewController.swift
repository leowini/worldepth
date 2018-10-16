//
//  ExploreViewController.swift
//  Worldepth
//
//  Created by Soren Dahl on 10/10/18.
//  Copyright © 2018 Soren Dahl. All rights reserved.
//

import UIKit
import SceneKit
import os.log

class ExploreViewController: UITableViewController {

    //MARK: Properties
    var posts = [PostEnvironment]()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        //load sample posts
        loadSamplePosts()
    }

    // MARK: - Table view data source

    override func numberOfSections(in tableView: UITableView) -> Int {
        
        return 1
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return posts.count
    }

    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cellIdentifier = "Post Cell"
        
        guard let cell = tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath) as? PostTableCell else {
            fatalError("dequed cell not an instance of PostTableCell")
        }

        let post = posts[indexPath.row]
        
        cell.authorLabel.text = post.author
        cell.titleLabel.text = post.title
        cell.ratingLabel.text = String(post.rating)
        cell.thumbnail.image = post.thumbnail
        // Configure the cell...

        return cell
    }
    

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        super.prepare(for: segue, sender: sender)
        
        switch segue.identifier ?? "" {
        case "ShowDetail":
            guard let postDetailViewController = segue.destination as? PostDetailViewController else {
                fatalError("Unexpected destination \(segue.destination)")
            }
            
            guard let selectedPostCell = sender as? PostTableCell else {
                fatalError("Unexpected sender \(String(describing: sender))")
            }
            
            guard let indexPath = tableView.indexPath(for: selectedPostCell) else {
                fatalError("The selected cell is not being displayed by the table")
            }
            
            let selectedPost = posts[indexPath.row]
            postDetailViewController.post = selectedPost
            
        default:
            fatalError("Unexpected segue identifier in \(String(describing: segue.identifier))")
        }
        
    }
    

    //MARK: Private Methods
    private func loadSamplePosts() {
        //load thumbnails
        let photo1 = UIImage(named: "Cathedral")
        let photo2 = UIImage(named: "School")
        let chairModel = SCNScene(/*named: "sword.dae"*/)
        /*let asset = MDLAsset(url: URL(string: "SceneKit Asset Catalog.scnassets/sword.dae")!)*/
        
        
        
        let boxGeometry = SCNBox(width: 10.0, height: 10.0, length: 10.0, chamferRadius: 1.0)
        let boxNode = SCNNode(geometry: boxGeometry)
        let tube = SCNTube(innerRadius: 10.0, outerRadius: 15.0, height: 20.0)
        let tubeNode = SCNNode(geometry: tube)
        chairModel.rootNode.addChildNode(boxNode)
        chairModel.rootNode.addChildNode(tubeNode)
        
        guard let post1 = PostEnvironment(author: "Bob", title: "Cathedral", model: chairModel, thumbnail: photo1, rating: 3) else {
            fatalError("Unable to initialize post1")
        }
        
        guard let post2 = PostEnvironment(author: "Sally", title: "School", model: chairModel, thumbnail: photo2, rating: 4) else {
            fatalError("unable to initialize post2")
        }
        
        posts.append(post1)
        posts.append(post2)
    }
    
}
