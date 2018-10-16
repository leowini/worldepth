//
//  PostTableCellTableViewCell.swift
//  Worldepth
//
//  Created by Soren Dahl on 10/10/18.
//  Copyright © 2018 Soren Dahl. All rights reserved.
//

import UIKit

class PostTableCell: UITableViewCell {

    //MARK: Properties

    @IBOutlet weak var thumbnail: UIImageView!
    @IBOutlet weak var titleLabel: UILabel!
    @IBOutlet weak var authorLabel: UILabel!
    @IBOutlet weak var ratingLabel: UILabel!
    
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
