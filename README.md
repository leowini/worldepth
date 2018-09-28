# worldepth2

Where to look for research:
LOOK ON TRELLO FOR INFO ABOUT WHERE INFO ABOUT YOUR TOPIC IS IN THE REPO. IF YOU CAN'T GET ACCESS TO THE TRELLO BOARD, LET LEO KNOW IN SLACK.


Getting Started:
1. Go to Research/Papers and skim the SlamForDummiesSuperBasic pdf. This is not exactly what we are doing because they used a robot moving in two dimension equipped with a laser depth sensor. We are just going to be using a phone camera. Even so, this is a good overview.
2. Look in Research/Notes/SLAMSegments for some notes on each of the 7 major parts of the Simultaneous Localization and Mapping algorithm. The most important parts to understand are (obviously) the localization and mapping parts. These core components of the SLAM algorithm are outlined in SLAMSegment #'s 2, 3, and 4. 5, 6, and 7 are basically icing on the cake for better accuracy.
3. If you want even more info about SLAM, go into Research/Papers and read the SLAM survey paper that says READ THIS ONE. This is what I used to take most of the SLAMSegment notes. It also gives some information on what kinds of choices the primary open-source SLAM apps made. (The two biggest/most recent open-source SLAM systems are called LSD-SLAM and ORB-2 SLAM.)
4. Look at the Orb-Slam2 code. In my opinion, it's easier to read than the LSD-SLAM implementation.

Developing For IOS:
1. Download XCode if you have a Mac
2. Developing for IOS is centered around Objective-C (If you know C, picking up Objective-C will be pretty simple). Its quite easy to find C tutorials online. For example, https://learncodethehardway.org/ is a good site to look at.
3. More to come

Git Logistics & Reminders:
1. When making changes, make a new branch to add your changes
2. Naming schema: 'Initials'-'Issue#'-'ShortDescription'
3. File Issues!!!