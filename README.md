# worldepth2
Getting Started:
1. Look at 3D examples.docx in the Research folder. This shows some basic 3D reconstruction examples and how the SLAM (Simultaneous Localization and Mapping) algorithm has evolved from aerial reconstructions.
2. Open Research/Links.docx and watch the first video link that says WATCH THIS ONE. Only watch the first part - the second half of the video is people asking inaudible questions and it stops making sense.
3. Go to Research/Papers and read/skim the SlamForDummiesSuperBasic pdf. This is not exactly what we are doing because they used a robot moving in two dimension equipped with a laser depth sensor. We are just going to be using a phone camera. Even so, this is a good overview.
4. Look in Research/Notes/SLAMSegments for some notes I took on each of the 7 major parts of the Simultaneous Localization and Mapping algorithm. The most important parts to understand are (obviously) the localization and mapping parts. These core components of the SLAM algorithm are outlined in SLAMSegment #'s 2, 3, and 4. 5, 6, and 7 are basically icing on the cake for better accuracy.
5. If you want even more info about SLAM, you can go into Research/Papers and read the SLAM survey paper that says READ THIS ONE. This is what I used to take most of the SLAMSegment notes. It also gives some information on what kinds of choices the primary open-source SLAM apps made. (The two biggest/most recent open-source SLAM systems are called LSD-SLAM and ORB-2 SLAM.)
6. Look at the Orb-Slam2 code. In my opinion, it's easier to read than the LSD-SLAM implementation.

Developing For IOS:
1. Download Xamarin 
2. Developing for IOS is centered around Objective-C (If you know C, picking up Objective-C will be pretty simple). Its quite easy to find C tutorials online. For example, https://learncodethehardway.org/ is a good site to look at.
3. More to come