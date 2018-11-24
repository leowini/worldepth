/*M///////////////////////////////////////////////////////////////////////////////////////
//
//  IMPORTANT: READ BEFORE DOWNLOADING, COPYING, INSTALLING OR USING.
//
//  By downloading, copying, installing or using the software you agree to this license.
//  If you do not agree to this license, do not download, install,
//  copy or use the software.
//
//
//                          License Agreement
//                For Open Source Computer Vision Library
//
// Copyright (C) 2000-2008, Intel Corporation, all rights reserved.
// Copyright (C) 2009, Willow Garage Inc., all rights reserved.
// Third party copyrights are property of their respective owners.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
//   * Redistribution's of source code must retain the above copyright notice,
//     this list of conditions and the following disclaimer.
//
//   * Redistribution's in binary form must reproduce the above copyright notice,
//     this list of conditions and the following disclaimer in the documentation
//     and/or other materials provided with the distribution.
//
//   * The name of the copyright holders may not be used to endorse or promote products
//     derived from this software without specific prior written permission.
//
// This software is provided by the copyright holders and contributors "as is" and
// any express or implied warranties, including, but not limited to, the implied
// warranties of merchantability and fitness for a particular purpose are disclaimed.
// In no event shall the Intel Corporation or contributors be liable for any direct,
// indirect, incidental, special, exemplary, or consequential damages
// (including, but not limited to, procurement of substitute goods or services;
// loss of use, data, or profits; or business interruption) however caused
// and on any theory of liability, whether in contract, strict liability,
// or tort (including negligence or otherwise) arising in any way out of
// the use of this software, even if advised of the possibility of such damage.
//
//M*/

#ifndef OPENCV_VIDEOIO_HPP
#define OPENCV_VIDEOIO_HPP

#include "opencv2/core.hpp"

/**
  @defgroup videoio Video I/O

  @brief Read and write video or images sequence with OpenCV

  ### See also:
  - @ref videoio_overview
  - Tutorials: @ref tutorial_table_of_content_videoio
  @{
    @defgroup videoio_flags_base Flags for video I/O
    @defgroup videoio_flags_others Additional flags for video I/O API backends
    @defgroup videoio_c C API for video I/O
    @defgroup videoio_ios iOS glue for video I/O
    @defgroup videoio_winrt WinRT glue for video I/O
    @defgroup videoio_registry Query I/O API backends registry
  @}
*/

////////////////////////////////// video io /////////////////////////////////

typedef struct CvCapture CvCapture;
typedef struct CvVideoWriter CvVideoWriter;

namespace cv
{

//! @addtogroup videoio
//! @{

//! @addtogroup videoio_flags_base
//! @{


/** @brief %VideoCapture API backends identifier.

Select preferred API for a capture object.
To be used in the VideoCapture::VideoCapture() constructor or VideoCapture::open()

@note Backends are available only if they have been built with your OpenCV binaries.
See @ref videoio_overview for more information.
*/
enum VideoCaptureAPIs {
       CAP_ANY          = 0,            //!< Auto detect == 0
       CAP_VFW          = 200,          //!< Video For Windows (platform native)
       CAP_V4L          = 200,          //!< V4L/V4L2 capturing support via libv4l
       CAP_V4L2         = CAP_V4L,      //!< Same as CAP_V4L
       CAP_FIREWIRE     = 300,          //!< IEEE 1394 drivers
       CAP_FIREWARE     = CAP_FIREWIRE, //!< Same as CAP_FIREWIRE
       CAP_IEEE1394     = CAP_FIREWIRE, //!< Same as CAP_FIREWIRE
       CAP_DC1394       = CAP_FIREWIRE, //!< Same as CAP_FIREWIRE
       CAP_CMU1394      = CAP_FIREWIRE, //!< Same as CAP_FIREWIRE
       CAP_QT           = 500,          //!< QuickTime
       CAP_UNICAP       = 600,          //!< Unicap drivers
       CAP_DSHOW        = 700,          //!< DirectShow (via videoInput)
       CAP_PVAPI        = 800,          //!< PvAPI, Prosilica GigE SDK
       CAP_OPENNI       = 900,          //!< OpenNI (for Kinect)
       CAP_OPENNI_ASUS  = 910,          //!< OpenNI (for Asus Xtion)
       CAP_ANDROID      = 1000,         //!< Android - not used
       CAP_XIAPI        = 1100,         //!< XIMEA Camera API
       CAP_AVFOUNDATION = 1200,         //!< AVFoundation framework for iOS (OS X Lion will have the same API)
       CAP_GIGANETIX    = 1300,         //!< Smartek Giganetix GigEVisionSDK
       CAP_MSMF         = 1400,         //!< Microsoft Media Foundation (via videoInput)
       CAP_WINRT        = 1410,         //!< Microsoft Windows Runtime using Media Foundation
       CAP_INTELPERC    = 1500,         //!< Intel Perceptual Computing SDK
       CAP_OPENNI2      = 1600,         //!< OpenNI2 (for Kinect)
       CAP_OPENNI2_ASUS = 1610,         //!< OpenNI2 (for Asus Xtion and Occipital Structure sensors)
       CAP_GPHOTO2      = 1700,         //!< gPhoto2 connection
       CAP_GSTREAMER    = 1800,         //!< GStreamer
       CAP_FFMPEG       = 1900,         //!< Open and record video file or stream using the FFMPEG library
       CAP_IMAGES       = 2000,         //!< OpenCV Image Sequence (e.g. img_%02d.jpg)
       CAP_ARAVIS       = 2100,         //!< Aravis SDK
       CAP_OPENCV_MJPEG = 2200,         //!< Built-in OpenCV MotionJPEG codec
       CAP_INTEL_MFX    = 2300,         //!< Intel MediaSDK
       CAP_XINE         = 2400,         //!< XINE engine (Linux)
     };

/** @brief %VideoCapture generic properties identifier.

 Reading / writing properties involves many layers. Some unexpected result might happens along this chain.
 Effective behaviour depends from device hardware, driver and API Backend.
 @sa videoio_flags_others, VideoCapture::get(), VideoCapture::set()
*/
enum VideoCaptureProperties {
       CAP_PROP_POS_MSEC       =0, //!< Current position of the video file in milliseconds.
       CAP_PROP_POS_FRAMES     =1, //!< 0-based index of the frame to be decoded/captured next.
       CAP_PROP_POS_AVI_RATIO  =2, //!< Relative position of the video file: 0=start of the film, 1=end of the film.
       CAP_PROP_FRAME_WIDTH    =3, //!< Width of the frames in the video stream.
       CAP_PROP_FRAME_HEIGHT   =4, //!< Height of the frames in the video stream.
       CAP_PROP_FPS            =5, //!< Frame rate.
       CAP_PROP_FOURCC         =6, //!< 4-character code of codec. see VideoWriter::fourcc .
       CAP_PROP_FRAME_COUNT    =7, //!< Number of frames in the video file.
       CAP_PROP_FORMAT         =8, //!< Format of the %Mat objects returned by VideoCapture::retrieve().
       CAP_PROP_MODE           =9, //!< Backend-specific value indicating the current capture mode.
       CAP_PROP_BRIGHTNESS    =10, //!< Brightness of the image (only for those cameras that support).
       CAP_PROP_CONTRAST      =11, //!< Contrast of the image (only for cameras).
       CAP_PROP_SATURATION    =12, //!< Saturation of the image (only for cameras).
       CAP_PROP_HUE           =13, //!< Hue of the image (only for cameras).
       CAP_PROP_GAIN          =14, //!< Gain of the image (only for those cameras that support).
       CAP_PROP_EXPOSURE      =15, //!< Exposure (only for those cameras that support).
       CAP_PROP_CONVERT_RGB   =16, //!< Boolean flags indicating whether images should be converted to RGB.
       CAP_PROP_WHITE_BALANCE_BLUE_U =17, //!< Currently unsupported.
       CAP_PROP_RECTIFICATION =18, //!< Rectification flag for stereo cameras (note: only supported by DC1394 v 2.x backend currently).
       CAP_PROP_MONOCHROME    =19,
       CAP_PROP_SHARPNESS     =20,
       CAP_PROP_AUTO_EXPOSURE =21, //!< DC1394: exposure control done by camera, user can adjust reference level using this feature.
       CAP_PROP_GAMMA         =22,
       CAP_PROP_TEMPERATURE   =23,
       CAP_PROP_TRIGGER       =24,
       CAP_PROP_TRIGGER_DELAY =25,
       CAP_PROP_WHITE_BALANCE_RED_V =26,
       CAP_PROP_ZOOM          =27,
       CAP_PROP_FOCUS         =28,
       CAP_PROP_GUID          =29,
       CAP_PROP_ISO_SPEED     =30,
       CAP_PROP_BACKLIGHT     =32,
       CAP_PROP_PAN           =33,
       CAP_PROP_TILT          =34,
       CAP_PROP_ROLL          =35,
       CAP_PROP_IRIS          =36,
       CAP_PROP_SETTINGS      =37, //!< Pop up video/camera filter dialog (note: only supported by DSHOW backend currently. The property value is ignored)
       CAP_PROP_BUFFERSIZE    =38,
       CAP_PROP_AUTOFOCUS     =39,
       CAP_PROP_SAR_NUM       =40, //!< Sample aspect ratio: num/den (num)
       CAP_PROP_SAR_DEN       =41, //!< Sample aspect ratio: num/den (den)
#ifndef CV_DOXYGEN
       CV__CAP_PROP_LATEST
#endif
     };


/** @brief Generic camera output modes identifier.
@note Currently, these are supported through the libv4l backend only.
*/
enum VideoCaptureModes {
       CAP_MODE_BGR  = 0, //!< BGR24 (default)
       CAP_MODE_RGB  = 1, //!< RGB24
       CAP_MODE_GRAY = 2, //!< Y8
       CAP_MODE_YUYV = 3  //!< YUYV
     };

/** @brief %VideoWriter generic properties identifier.
 @sa VideoWriter::get(), VideoWriter::set()
*/
enum VideoWriterProperties {
  VIDEOWRITER_PROP_QUALITY = 1,    //!< Current quality (0..100%) of the encoded videostream. Can be adjusted dynamically in some codecs.
  VIDEOWRITER_PROP_FRAMEBYTES = 2, //!< (Read-only): Size of just encoded video frame. Note that the encoding order may be different from representation order.
  VIDEOWRITER_PROP_NSTRIPES = 3    //!< Number of stripes for parallel encoding. -1 for auto detection.
};

//! @} videoio_flags_base

//! @addtogroup videoio_flags_others
//! @{

/** @name IEEE 1394 drivers
    @{
*/

/** @brief Modes of the IEEE 1394 controlling registers
(can be: auto, manual, auto single push, absolute Latter allowed with any other mode)
every feature can have only one mode turned on at a time
*/
enum { CAP_PROP_DC1394_OFF                = -4, //!< turn the feature off (not controlled manually nor automatically).
       CAP_PROP_DC1394_MODE_MANUAL        = -3, //!< set automatically when a value of the feature is set by the user.
       CAP_PROP_DC1394_MODE_AUTO          = -2,
       CAP_PROP_DC1394_MODE_ONE_PUSH_AUTO = -1,
       CAP_PROP_DC1394_MAX                = 31
     };

//! @} IEEE 1394 drivers

/** @name OpenNI (for Kinect)
    @{
*/

//! OpenNI map generators
enum { CAP_OPENNI_DEPTH_GENERATOR = 1 << 31,
       CAP_OPENNI_IMAGE_GENERATOR = 1 << 30,
       CAP_OPENNI_IR_GENERATOR    = 1 << 29,
       CAP_OPENNI_GENERATORS_MASK = CAP_OPENNI_DEPTH_GENERATOR + CAP_OPENNI_IMAGE_GENERATOR + CAP_OPENNI_IR_GENERATOR
     };

//! Properties of cameras available through OpenNI backend
enum { CAP_PROP_OPENNI_OUTPUT_MODE       = 100,
       CAP_PROP_OPENNI_FRAME_MAX_DEPTH   = 101, //!< In mm
       CAP_PROP_OPENNI_BASELINE          = 102, //!< In mm
       CAP_PROP_OPENNI_FOCAL_LENGTH      = 103, //!< In pixels
       CAP_PROP_OPENNI_REGISTRATION      = 104, //!< Flag that synchronizes the remapping depth map to image map
                                                //!< by changing depth generator's view point (if the flag is "on") or
                                                //!< sets this view point to its normal one (if the flag is "off").
       CAP_PROP_OPENNI_REGISTRATION_ON   = CAP_PROP_OPENNI_REGISTRATION,
       CAP_PROP_OPENNI_APPROX_FRAME_SYNC = 105,
       CAP_PROP_OPENNI_MAX_BUFFER_SIZE   = 106,
       CAP_PROP_OPENNI_CIRCLE_BUFFER     = 107,
       CAP_PROP_OPENNI_MAX_TIME_DURATION = 108,
       CAP_PROP_OPENNI_GENERATOR_PRESENT = 109,
       CAP_PROP_OPENNI2_SYNC             = 110,
       CAP_PROP_OPENNI2_MIRROR           = 111
     };

//! OpenNI shortcuts
enum { CAP_OPENNI_IMAGE_GENERATOR_PRESENT         = CAP_OPENNI_IMAGE_GENERATOR + CAP_PROP_OPENNI_GENERATOR_PRESENT,
       CAP_OPENNI_IMAGE_GENERATOR_OUTPUT_MODE     = CAP_OPENNI_IMAGE_GENERATOR + CAP_PROP_OPENNI_OUTPUT_MODE,
       CAP_OPENNI_DEPTH_GENERATOR_PRESENT         = CAP_OPENNI_DEPTH_GENERATOR + CAP_PROP_OPENNI_GENERATOR_PRESENT,
       CAP_OPENNI_DEPTH_GENERATOR_BASELINE        = CAP_OPENNI_DEPTH_GENERATOR + CAP_PROP_OPENNI_BASELINE,
       CAP_OPENNI_DEPTH_GENERATOR_FOCAL_LENGTH    = CAP_OPENNI_DEPTH_GENERATOR + CAP_PROP_OPENNI_FOCAL_LENGTH,
       CAP_OPENNI_DEPTH_GENERATOR_REGISTRATION    = CAP_OPENNI_DEPTH_GENERATOR + CAP_PROP_OPENNI_REGISTRATION,
       CAP_OPENNI_DEPTH_GENERATOR_REGISTRATION_ON = CAP_OPENNI_DEPTH_GENERATOR_REGISTRATION,
       CAP_OPENNI_IR_GENERATOR_PRESENT            = CAP_OPENNI_IR_GENERATOR + CAP_PROP_OPENNI_GENERATOR_PRESENT,
     };

//! OpenNI data given from depth generator
enum { CAP_OPENNI_DEPTH_MAP         = 0, //!< Depth values in mm (CV_16UC1)
       CAP_OPENNI_POINT_CLOUD_MAP   = 1, //!< XYZ in meters (CV_32FC3)
       CAP_OPENNI_DISPARITY_MAP     = 2, //!< Disparity in pixels (CV_8UC1)
       CAP_OPENNI_DISPARITY_MAP_32F = 3, //!< Disparity in pixels (CV_32FC1)
       CAP_OPENNI_VALID_DEPTH_MASK  = 4, //!< CV_8UC1

       CAP_OPENNI_BGR_IMAGE         = 5, //!< Data given from RGB image generator
       CAP_OPENNI_GRAY_IMAGE        = 6, //!< Data given from RGB image generator

       CAP_OPENNI_IR_IMAGE          = 7  //!< Data given from IR image generator
     };

//! Supported output modes of OpenNI image generator
enum { CAP_OPENNI_VGA_30HZ  = 0,
       CAP_OPENNI_SXGA_15HZ = 1,
       CAP_OPENNI_SXGA_30HZ = 2,
       CAP_OPENNI_QVGA_30HZ = 3,
       CAP_OPENNI_QVGA_60HZ = 4
     };

//! @} OpenNI

/** @name GStreamer
    @{
*/

enum { CAP_PROP_GSTREAMER_QUEUE_LENGTH = 200 //!< Default is 1
     };

//! @} GStreamer

/** @name PvAPI, Prosilica GigE SDK
    @{
*/

//! PVAPI
enum { CAP_PROP_PVAPI_MULTICASTIP           = 300, //!< IP for enable multicast master mode. 0 for disable multicast.
       CAP_PROP_PVAPI_FRAMESTARTTRIGGERMODE = 301, //!< FrameStartTriggerMode: Determines how a frame is initiated.
       CAP_PROP_PVAPI_DECIMATIONHORIZONTAL  = 302, //!< Horizontal sub-sampling of the image.
       CAP_PROP_PVAPI_DECIMATIONVERTICAL    = 303, //!< Vertical sub-sampling of the image.
       CAP_PROP_PVAPI_BINNINGX              = 304, //!< Horizontal binning factor.
       CAP_PROP_PVAPI_BINNINGY              = 305, //!< Vertical binning factor.
       CAP_PROP_PVAPI_PIXELFORMAT           = 306  //!< Pixel format.
     };

//! PVAPI: FrameStartTriggerMode
enum { CAP_PVAPI_FSTRIGMODE_FREERUN     = 0,    //!< Freerun
       CAP_PVAPI_FSTRIGMODE_SYNCIN1     = 1,    //!< SyncIn1
       CAP_PVAPI_FSTRIGMODE_SYNCIN2     = 2,    //!< SyncIn2
       CAP_PVAPI_FSTRIGMODE_FIXEDRATE   = 3,    //!< FixedRate
       CAP_PVAPI_FSTRIGMODE_SOFTWARE    = 4     //!< Software
     };

//! PVAPI: DecimationHorizontal, DecimationVertical
enum { CAP_PVAPI_DECIMATION_OFF       = 1,    //!< Off
       CAP_PVAPI_DECIMATION_2OUTOF4   = 2,    //!< 2 out of 4 decimation
       CAP_PVAPI_DECIMATION_2OUTOF8   = 4,    //!< 2 out of 8 decimation
       CAP_PVAPI_DECIMATION_2OUTOF16  = 8     //!< 2 out of 16 decimation
     };

//! PVAPI: PixelFormat
enum { CAP_PVAPI_PIXELFORMAT_MONO8    = 1,    //!< Mono8
       CAP_PVAPI_PIXELFORMAT_MONO16   = 2,    //!< Mono16
       CAP_PVAPI_PIXELFORMAT_BAYER8   = 3,    //!< Bayer8
       CAP_PVAPI_PIXELFORMAT_BAYER16  = 4,    //!< Bayer16
       CAP_PVAPI_PIXELFORMAT_RGB24    = 5,    //!< Rgb24
       CAP_PVAPI_PIXELFORMAT_BGR24    = 6,    //!< Bgr24
       CAP_PVAPI_PIXELFORMAT_RGBA32   = 7,    //!< Rgba32
       CAP_PVAPI_PIXELFORMAT_BGRA32   = 8,    //!< Bgra32
     };

//! @} PvAPI

/** @name XIMEA Camera API
    @{
*/

//! Properties of cameras available through XIMEA SDK backend
enum { CAP_PROP_XI_DOWNSAMPLING                                 = 400, //!< Change image resolution by binning or skipping.
       CAP_PROP_XI_DATA_FORMAT                                  = 401, //!< Output data format.
       CAP_PROP_XI_OFFSET_X                                     = 402, //!< Horizontal offset from the origin to the area of interest (in pixels).
       CAP_PROP_XI_OFFSET_Y                                     = 403, //!< Vertical offset from the origin to the area of interest (in pixels).
       CAP_PROP_XI_TRG_SOURCE                                   = 404, //!< Defines source of trigger.
       CAP_PROP_XI_TRG_SOFTWARE                                 = 405, //!< Generates an internal trigger. PRM_TRG_SOURCE must be set to TRG_SOFTWARE.
       CAP_PROP_XI_GPI_SELECTOR                                 = 406, //!< Selects general purpose input.
       CAP_PROP_XI_GPI_MODE                                     = 407, //!< Set general purpose input mode.
       CAP_PROP_XI_GPI_LEVEL                                    = 408, //!< Get general purpose level.
       CAP_PROP_XI_GPO_SELECTOR                                 = 409, //!< Selects general purpose output.
       CAP_PROP_XI_GPO_MODE                                     = 410, //!< Set general purpose output mode.
       CAP_PROP_XI_LED_SELECTOR                                 = 411, //!< Selects camera signalling LED.
       CAP_PROP_XI_LED_MODE                                     = 412, //!< Define camera signalling LED functionality.
       CAP_PROP_XI_MANUAL_WB                                    = 413, //!< Calculates White Balance(must be called during acquisition).
       CAP_PROP_XI_AUTO_WB                                      = 414, //!< Automatic white balance.
       CAP_PROP_XI_AEAG                                         = 415, //!< Automatic exposure/gain.
       CAP_PROP_XI_EXP_PRIORITY                                 = 416, //!< Exposure priority (0.5 - exposure 50%, gain 50%).
       CAP_PROP_XI_AE_MAX_LIMIT                                 = 417, //!< Maximum limit of exposure in AEAG procedure.
       CAP_PROP_XI_AG_MAX_LIMIT                                 = 418, //!< Maximum limit of gain in AEAG procedure.
       CAP_PROP_XI_AEAG_LEVEL                                   = 419, //!< Average intensity of output signal AEAG should achieve(in %).
       CAP_PROP_XI_TIMEOUT                                      = 420, //!< Image capture timeout in milliseconds.
       CAP_PROP_XI_EXPOSURE                                     = 421, //!< Exposure time in microseconds.
       CAP_PROP_XI_EXPOSURE_BURST_COUNT                         = 422, //!< Sets the number of times of exposure in one frame.
       CAP_PROP_XI_GAIN_SELECTOR                                = 423, //!< Gain selector for parameter Gain allows to select different type of gains.
       CAP_PROP_XI_GAIN                                         = 424, //!< Gain in dB.
       CAP_PROP_XI_DOWNSAMPLING_TYPE                            = 426, //!< Change image downsampling type.
       CAP_PROP_XI_BINNING_SELECTOR                             = 427, //!< Binning engine selector.
       CAP_PROP_XI_BINNING_VERTICAL                             = 428, //!< Vertical Binning - number of vertical photo-sensitive cells to combine together.
       CAP_PROP_XI_BINNING_HORIZONTAL                           = 429, //!< Horizontal Binning - number of horizontal photo-sensitive cells to combine together.
       CAP_PROP_XI_BINNING_PATTERN                              = 430, //!< Binning pattern type.
       CAP_PROP_XI_DECIMATION_SELECTOR                          = 431, //!< Decimation engine selector.
       CAP_PROP_XI_DECIMATION_VERTICAL                          = 432, //!< Vertical Decimation - vertical sub-sampling of the image - reduces the vertical resolution of the image by the specified vertical decimation factor.
       CAP_PROP_XI_DECIMATION_HORIZONTAL                        = 433, //!< Horizontal Decimation - horizontal sub-sampling of the image - reduces the horizontal resolution of the image by the specified vertical decimation factor.
       CAP_PROP_XI_DECIMATION_PATTERN                           = 434, //!< Decimation pattern type.
       CAP_PROP_XI_TEST_PATTERN_GENERATOR_SELECTOR              = 587, //!< Selects which test pattern generator is controlled by the TestPattern feature.
       CAP_PROP_XI_TEST_PATTERN                                 = 588, //!< Selects which test pattern type is generated by the selected generator.
       CAP_PROP_XI_IMAGE_DATA_FORMAT                            = 435, //!< Output data format.
       CAP_PROP_XI_SHUTTER_TYPE                                 = 436, //!< Change sensor shutter type(CMOS sensor).
       CAP_PROP_XI_SENSOR_TAPS                                  = 437, //!< Number of taps.
       CAP_PROP_XI_AEAG_ROI_OFFSET_X                            = 439, //!< Automatic exposure/gain ROI offset X.
       CAP_PROP_XI_AEAG_ROI_OFFSET_Y                            = 440, //!< Automatic exposure/gain ROI offset Y.
       CAP_PROP_XI_AEAG_ROI_WIDTH                               = 441, //!< Automatic exposure/gain ROI Width.
       CAP_PROP_XI_AEAG_ROI_HEIGHT                              = 442, //!< Automatic exposure/gain ROI Height.
       CAP_PROP_XI_BPC                                          = 445, //!< Correction of bad pixels.
       CAP_PROP_XI_WB_KR                                        = 448, //!< White balance red coefficient.
       CAP_PROP_XI_WB_KG                                        = 449, //!< White balance green coefficient.
       CAP_PROP_XI_WB_KB                                        = 450, //!< White balance blue coefficient.
       CAP_PROP_XI_WIDTH                                        = 451, //!< Width of the Image provided by the device (in pixels).
       CAP_PROP_XI_HEIGHT                                       = 452, //!< Height of the Image provided by the device (in pixels).
       CAP_PROP_XI_REGION_SELECTOR                              = 589, //!< Selects Region in Multiple ROI which parameters are set by width, height, ... ,region mode.
       CAP_PROP_XI_REGION_MODE                                  = 595, //!< Activates/deactivates Region selected by Region Selector.
       CAP_PROP_XI_LIMIT_BANDWIDTH                              = 459, //!< Set/get bandwidth(datarate)(in Megabits).
       CAP_PROP_XI_SENSOR_DATA_BIT_DEPTH                        = 460, //!< Sensor output data bit depth.
       CAP_PROP_XI_OUTPUT_DATA_BIT_DEPTH                        = 461, //!< Device output data bit depth.
       CAP_PROP_XI_IMAGE_DATA_BIT_DEPTH                         = 462, //!< bitdepth of data returned by function xiGetImage.
       CAP_PROP_XI_OUTPUT_DATA_PACKING                          = 463, //!< Device output data packing (or grouping) enabled. Packing could be enabled if output_data_bit_depth > 8 and packing capability is available.
       CAP_PROP_XI_OUTPUT_DATA_PACKING_TYPE                     = 464, //!< Data packing type. Some cameras supports only specific packing type.
       CAP_PROP_XI_IS_COOLED                                    = 465, //!< Returns 1 for cameras that support cooling.
       CAP_PROP_XI_COOLING                                      = 466, //!< Start camera cooling.
       CAP_PROP_XI_TARGET_TEMP                                  = 467, //!< Set sensor target temperature for cooling.
       CAP_PROP_XI_CHIP_TEMP                                    = 468, //!< Camera sensor temperature.
       CAP_PROP_XI_HOUS_TEMP                                    = 469, //!< Camera housing temperature.
       CAP_PROP_XI_HOUS_BACK_SIDE_TEMP                          = 590, //!< Camera housing back side temperature.
       CAP_PROP_XI_SENSOR_BOARD_TEMP                            = 596, //!< Camera sensor board temperature.
       CAP_PROP_XI_CMS                                          = 470, //!< Mode of color management system.
       CAP_PROP_XI_APPLY_CMS                                    = 471, //!< Enable applying of CMS profiles to xiGetImage (see XI_PRM_INPUT_CMS_PROFILE, XI_PRM_OUTPUT_CMS_PROFILE).
       CAP_PROP_XI_IMAGE_IS_COLOR                               = 474, //!< Returns 1 for color cameras.
       CAP_PROP_XI_COLOR_FILTER_ARRAY                           = 475, //!< Returns color filter array type of RAW data.
       CAP_PROP_XI_GAMMAY                                       = 476, //!< Luminosity gamma.
       CAP_PROP_XI_GAMMAC                                       = 477, //!< Chromaticity gamma.
       CAP_PROP_XI_SHARPNESS                                    = 478, //!< Sharpness Strength.
       CAP_PROP_XI_CC_MATRIX_00                                 = 479, //!< Color Correction Matrix element [0][0].
       CAP_PROP_XI_CC_MATRIX_01                                 = 480, //!< Color Correction Matrix element [0][1].
       CAP_PROP_XI_CC_MATRIX_02                                 = 481, //!< Color Correction Matrix element [0][2].
       CAP_PROP_XI_CC_MATRIX_03                                 = 482, //!< Color Correction Matrix element [0][3].
       CAP_PROP_XI_CC_MATRIX_10                                 = 483, //!< Color Correction Matrix element [1][0].
       CAP_PROP_XI_CC_MATRIX_11                                 = 484, //!< Color Correction Matrix element [1][1].
       CAP_PROP_XI_CC_MATRIX_12                                 = 485, //!< Color Correction Matrix element [1][2].
       CAP_PROP_XI_CC_MATRIX_13                                 = 486, //!< Color Correction Matrix element [1][3].
       CAP_PROP_XI_CC_MATRIX_20                                 = 487, //!< Color Correction Matrix element [2][0].
       CAP_PROP_XI_CC_MATRIX_21                                 = 488, //!< Color Correction Matrix element [2][1].
       CAP_PROP_XI_CC_MATRIX_22                                 = 489, //!< Color Correction Matrix element [2][2].
       CAP_PROP_XI_CC_MATRIX_23                                 = 490, //!< Color Correction Matrix element [2][3].
       CAP_PROP_XI_CC_MATRIX_30                                 = 491, //!< Color Correction Matrix element [3][0].
       CAP_PROP_XI_CC_MATRIX_31                                 = 492, //!< Color Correction Matrix element [3][1].
       CAP_PROP_XI_CC_MATRIX_32                                 = 493, //!< Color Correction Matrix element [3][2].
       CAP_PROP_XI_CC_MATRIX_33                                 = 494, //!< Color Correction Matrix element [3][3].
       CAP_PROP_XI_DEFAULT_CC_MATRIX                            = 495, //!< Set default Color Correction Matrix.
       CAP_PROP_XI_TRG_SELECTOR                                 = 498, //!< Selects the type of trigger.
       CAP_PROP_XI_ACQ_FRAME_BURST_COUNT                        = 499, //!< Sets number of frames acquired by burst. This burst is used only if trigger is set to FrameBurstStart.
       CAP_PROP_XI_DEBOUNCE_EN                                  = 507, //!< Enable/Disable debounce to selected GPI.
       CAP_PROP_XI_DEBOUNCE_T0                                  = 508, //!< Debounce time (x * 10us).
       CAP_PROP_XI_DEBOUNCE_T1                                  = 509, //!< Debounce time (x * 10us).
       CAP_PROP_XI_DEBOUNCE_POL                                 = 510, //!< Debounce polarity (pol = 1 t0 - falling edge, t1 - rising edge).
       CAP_PROP_XI_LENS_MODE                                    = 511, //!< Status of lens control interface. This shall be set to XI_ON before any Lens operations.
       CAP_PROP_XI_LENS_APERTURE_VALUE                          = 512, //!< Current lens aperture value in stops. Examples: 2.8, 4, 5.6, 8, 11.
       CAP_PROP_XI_LENS_FOCUS_MOVEMENT_VALUE                    = 513, //!< Lens current focus movement value to be used by XI_PRM_LENS_FOCUS_MOVE in motor steps.
       CAP_PROP_XI_LENS_FOCUS_MOVE                              = 514, //!< Moves lens focus motor by steps set in XI_PRM_LENS_FOCUS_MOVEMENT_VALUE.
       CAP_PROP_XI_LENS_FOCUS_DISTANCE                          = 515, //!< Lens focus distance in cm.
       CAP_PROP_XI_LENS_FOCAL_LENGTH                            = 516, //!< Lens focal distance in mm.
       CAP_PROP_XI_LENS_FEATURE_SELECTOR                        = 517, //!< Selects the current feature which is accessible by XI_PRM_LENS_FEATURE.
       CAP_PROP_XI_LENS_FEATURE                                 = 518, //!< Allows access to lens feature value currently selected by XI_PRM_LENS_FEATURE_SELECTOR.
       CAP_PROP_XI_DEVICE_MODEL_ID                              = 521, //!< Returns device model id.
       CAP_PROP_XI_DEVICE_SN                                    = 522, //!< Returns device serial number.
       CAP_PROP_XI_IMAGE_DATA_FORMAT_RGB32_ALPHA                = 529, //!< The alpha channel of RGB32 output image format.
       CAP_PROP_XI_IMAGE_PAYLOAD_SIZE                           = 530, //!< Buffer size in bytes sufficient for output image returned by xiGetImage.
       CAP_PROP_XI_TRANSPORT_PIXEL_FORMAT                       = 531, //!< Current format of pixels on transport layer.
       CAP_PROP_XI_SENSOR_CLOCK_FREQ_HZ                         = 532, //!< Sensor clock frequency in Hz.
       CAP_PROP_XI_SENSOR_CLOCK_FREQ_INDEX                      = 533, //!< Sensor clock frequency index. Sensor with selected frequencies have possibility to set the frequency only by this index.
       CAP_PROP_XI_SENSOR_OUTPUT_CHANNEL_COUNT                  = 534, //!< Number of output channels from sensor used for data transfer.
       CAP_PROP_XI_FRAMERATE                                    = 535, //!< Define framerate in Hz.
       CAP_PROP_XI_COUNTER_SELECTOR                             = 536, //!< Select counter.
       CAP_PROP_XI_COUNTER_VALUE                                = 537, //!< Counter status.
       CAP_PROP_XI_ACQ_TIMING_MODE                              = 538, //!< Type of sensor frames timing.
       CAP_PROP_XI_AVAILABLE_BANDWIDTH                          = 539, //!< Calculate and returns available interface bandwidth(int Megabits).
       CAP_PROP_XI_BUFFER_POLICY                                = 540, //!< Data move policy.
       CAP_PROP_XI_LUT_EN                                       = 541, //!< Activates LUT.
       CAP_PROP_XI_LUT_INDEX                                    = 542, //!< Control the index (offset) of the coefficient to access in the LUT.
       CAP_PROP_XI_LUT_VALUE                                    = 543, //!< Value at entry LUTIndex of the LUT.
       CAP_PROP_XI_TRG_DELAY                                    = 544, //!< Specifies the delay in microseconds (us) to apply after the trigger reception before activating it.
       CAP_PROP_XI_TS_RST_MODE                                  = 545, //!< Defines how time stamp reset engine will be armed.
       CAP_PROP_XI_TS_RST_SOURCE                                = 546, //!< Defines which source will be used for timestamp reset. Writing this parameter will trigger settings of engine (arming).
       CAP_PROP_XI_IS_DEVICE_EXIST                              = 547, //!< Returns 1 if camera connected and works properly.
       CAP_PROP_XI_ACQ_BUFFER_SIZE                              = 548, //!< Acquisition buffer size in buffer_size_unit. Default bytes.
       CAP_PROP_XI_ACQ_BUFFER_SIZE_UNIT                         = 549, //!< Acquisition buffer size unit in bytes. Default 1. E.g. Value 1024 means that buffer_size is in KiBytes.
       CAP_PROP_XI_ACQ_TRANSPORT_BUFFER_SIZE                    = 550, //!< Acquisition transport buffer size in bytes.
       CAP_PROP_XI_BUFFERS_QUEUE_SIZE                           = 551, //!< Queue of field/frame buffers.
       CAP_PROP_XI_ACQ_TRANSPORT_BUFFER_COMMIT                  = 552, //!< Number of buffers to commit to low level.
       CAP_PROP_XI_RECENT_FRAME                                 = 553, //!< GetImage returns most recent frame.
       CAP_PROP_XI_DEVICE_RESET                                 = 554, //!< Resets the camera to default state.
       CAP_PROP_XI_COLUMN_FPN_CORRECTION                        = 555, //!< Correction of column FPN.
       CAP_PROP_XI_ROW_FPN_CORRECTION                           = 591, //!< Correction of row FPN.
       CAP_PROP_XI_SENSOR_MODE                                  = 558, //!< Current sensor mode. Allows to select sensor mode by one integer. Setting of this parameter affects: image dimensions and downsampling.
       CAP_PROP_XI_HDR                                          = 559, //!< Enable High Dynamic Range feature.
       CAP_PROP_XI_HDR_KNEEPOINT_COUNT                          = 560, //!< The number of kneepoints in the PWLR.
       CAP_PROP_XI_HDR_T1                                       = 561, //!< Position of first kneepoint(in % of XI_PRM_EXPOSURE).
       CAP_PROP_XI_HDR_T2                                       = 562, //!< Position of second kneepoint (in % of XI_PRM_EXPOSURE).
       CAP_PROP_XI_KNEEPOINT1                                   = 563, //!< Value of first kneepoint (% of sensor saturation).
       CAP_PROP_XI_KNEEPOINT2                                   = 564, //!< Value of second kneepoint (% of sensor saturation).
       CAP_PROP_XI_IMAGE_BLACK_LEVEL                            = 565, //!< Last image black level counts. Can be used for Offline processing to recall it.
       CAP_PROP_XI_HW_REVISION                                  = 571, //!< Returns hardware revision number.
       CAP_PROP_XI_DEBUG_LEVEL                                  = 572, //!< Set debug level.
       CAP_PROP_XI_AUTO_BANDWIDTH_CALCULATION                   = 573, //!< Automatic bandwidth calculation.
       CAP_PROP_XI_FFS_FILE_ID                                  = 594, //!< File number.
       CAP_PROP_XI_FFS_FILE_SIZE                                = 580, //!< Size of file.
       CAP_PROP_XI_FREE_FFS_SIZE                                = 581, //!< Size of free camera FFS.
       CAP_PROP_XI_USED_FFS_SIZE                                = 582, //!< Size of used camera FFS.
       CAP_PROP_XI_FFS_ACCESS_KEY                               = 583, //!< Setting of key enables file operations on some cameras.
       CAP_PROP_XI_SENSOR_FEATURE_SELECTOR                      = 585, //!< Selects the current feature which is accessible by XI_PRM_SENSOR_FEATURE_VALUE.
       CAP_PROP_XI_SENSOR_FEATURE_VALUE                         = 586, //!< Allows access to sensor feature value currently selected by XI_PRM_SENSOR_FEATURE_SELECTOR.
     };

//! @} XIMEA

/** @name AVFoundation framework for iOS
    OS X Lion will have the same API
    @{
*/

//! Properties of cameras available through AVFOUNDATION backend
enum { CAP_PROP_IOS_DEVICE_FOCUS        = 9001,
       CAP_PROP_IOS_DEVICE_EXPOSURE     = 9002,
       CAP_PROP_IOS_DEVICE_FLASH        = 9003,
       CAP_PROP_IOS_DEVICE_WHITEBALANCE = 9004,
       CAP_PROP_IOS_DEVICE_TORCH        = 9005
     };

/** @name Smartek Giganetix GigEVisionSDK
    @{
*/

//! Properties of cameras available through Smartek Giganetix Ethernet Vision backend
/* --- Vladimir Litvinenko (litvinenko.vladimir@gmail.com) --- */
enum { CAP_PROP_GIGA_FRAME_OFFSET_X   = 10001,
       CAP_PROP_GIGA_FRAME_OFFSET_Y   = 10002,
       CAP_PROP_GIGA_FRAME_WIDTH_MAX  = 10003,
       CAP_PROP_GIGA_FRAME_HEIGH_MAX  = 10004,
       CAP_PROP_GIGA_FRAME_SENS_WIDTH = 10005,
       CAP_PROP_GIGA_FRAME_SENS_HEIGH = 10006
     };

//! @} Smartek

/** @name Intel Perceptual Computing SDK
    @{
*/
enum { CAP_PROP_INTELPERC_PROFILE_COUNT               = 11001,
       CAP_PROP_INTELPERC_PROFILE_IDX                 = 11002,
       CAP_PROP_INTELPERC_DEPTH_LOW_CONFIDENCE_VALUE  = 11003,
       CAP_PROP_INTELPERC_DEPTH_SATURATION_VALUE      = 11004,
       CAP_PROP_INTELPERC_DEPTH_CONFIDENCE_THRESHOLD  = 11005,
       CAP_PROP_INTELPERC_DEPTH_FOCAL_LENGTH_HORZ     = 11006,
       CAP_PROP_INTELPERC_DEPTH_FOCAL_LENGTH_VERT     = 11007
     };

//! Intel Perceptual Streams
enum { CAP_INTELPERC_DEPTH_GENERATOR = 1 << 29,
       CAP_INTELPERC_IMAGE_GENERATOR = 1 << 28,
       CAP_INTELPERC_GENERATORS_MASK = CAP_INTELPERC_DEPTH_GENERATOR + CAP_INTELPERC_IMAGE_GENERATOR
     };

enum { CAP_INTELPERC_DEPTH_MAP              = 0, //!< Each pixel is a 16-bit integer. The value indicates the distance from an object to the camera's XY plane or the Cartesian depth.
       CAP_INTELPERC_UVDEPTH_MAP            = 1, //!< Each pixel contains two 32-bit floating point values in the range of 0-1, representing the mapping of depth coordinates to the color coordinates.
       CAP_INTELPERC_IR_MAP                 = 2, //!< Each pixel is a 16-bit integer. The value indicates the intensity of the reflected laser beam.
       CAP_INTELPERC_IMAGE                  = 3
     };

//! @} Intel Perceptual

/** @name gPhoto2 connection
    @{
*/

/** @brief gPhoto2 properties

If `propertyId` is less than 0 then work on widget with that __additive inversed__ camera setting ID
Get IDs by using CAP_PROP_GPHOTO2_WIDGET_ENUMERATE.
@see CvCaptureCAM_GPHOTO2 for more info
*/
enum { CAP_PROP_GPHOTO2_PREVIEW           = 17001, //!< Capture only preview from liveview mode.
       CAP_PROP_GPHOTO2_WIDGET_ENUMERATE  = 17002, //!< Readonly, returns (const char *).
       CAP_PROP_GPHOTO2_RELOAD_CONFIG     = 17003, //!< Trigger, only by set. Reload camera settings.
       CAP_PROP_GPHOTO2_RELOAD_ON_CHANGE  = 17004, //!< Reload all settings on set.
       CAP_PROP_GPHOTO2_COLLECT_MSGS      = 17005, //!< Collect messages with details.
       CAP_PROP_GPHOTO2_FLUSH_MSGS        = 17006, //!< Readonly, returns (const char *).
       CAP_PROP_SPEED                     = 17007, //!< Exposure speed. Can be readonly, depends on camera program.
       CAP_PROP_APERTURE                  = 17008, //!< Aperture. Can be readonly, depends on camera program.
       CAP_PROP_EXPOSUREPROGRAM           = 17009, //!< Camera exposure program.
       CAP_PROP_VIEWFINDER                = 17010  //!< Enter liveview mode.
     };

//! @} gPhoto2


/** @name Images backend
    @{
*/

/** @brief Images backend properties

*/
enum { CAP_PROP_IMAGES_BASE = 18000,
       CAP_PROP_IMAGES_LAST = 19000 // excluding
     };

//! @} Images

//! @} videoio_flags_others


class IVideoCapture;

/** @brief Class for video capturing from video files, image sequences or cameras.

The class provides C++ API for capturing video from cameras or for reading video files and image sequences.

Here is how the class can be used:
@include samples/cpp/videocapture_basic.cpp

@note In @ref videoio_c "C API" the black-box structure `CvCapture` is used instead of %VideoCapture.
@note
-   (C++) A basic sample on using the %VideoCapture interface can be found at
    `OPENCV_SOURCE_CODE/samples/cpp/videocapture_starter.cpp`
-   (Python) A basic sample on using the %VideoCapture interface can be found at
    `OPENCV_SOURCE_CODE/samples/python/video.py`
-   (Python) A multi threaded video processing sample can be found at
    `OPENCV_SOURCE_CODE/samples/python/video_threaded.py`
-   (Python) %VideoCapture sample showcasing some features of the Video4Linux2 backend
    `OPENCV_SOURCE_CODE/samples/python/video_v4l2.py`
 */
class CV_EXPORTS_W VideoCapture
{
public:
    /** @brief Default constructor
    @note In @ref videoio_c "C API", when you finished working with video, release CvCapture structure with
    cvReleaseCapture(), or use Ptr\<CvCapture\> that calls cvReleaseCapture() automatically in the
    destructor.
     */
    CV_WRAP VideoCapture();

    /** @overload
    @brief  Open video file or a capturing device or a IP video stream for video capturing

    Same as VideoCapture(const String& filename, int apiPreference) but using default Capture API backends
    */
    CV_WRAP VideoCapture(const String& filename);

    /** @overload
    @brief  Open video file or a capturing device or a IP video stream for video capturing with API Preference

    @param filename it can be:
    - name of video file (eg. `video.avi`)
    - or image sequence (eg. `img_%02d.jpg`, which will read samples like `img_00.jpg, img_01.jpg, img_02.jpg, ...`)
    - or URL of video stream (eg. `protocol://host:port/script_name?script_params|auth`).
      Note that each video stream or IP camera feed has its own URL scheme. Please refer to the
      documentation of source stream to know the right URL.
    @param apiPreference preferred Capture API backends to use. Can be used to enforce a specific reader
    implementation if multiple are available: e.g. cv::CAP_FFMPEG or cv::CAP_IMAGES or cv::CAP_DSHOW.
    @sa The list of supported API backends cv::VideoCaptureAPIs
    */
    CV_WRAP VideoCapture(const String& filename, int apiPreference);

    /** @overload
    @brief  Open a camera for video capturing

    @param index camera_id + domain_offset (CAP_*) id of the video capturing device to open. To open default camera using default backend just pass 0.
    Use a `domain_offset` to enforce a specific reader implementation if multiple are available like cv::CAP_FFMPEG or cv::CAP_IMAGES or cv::CAP_DSHOW.
    e.g. to open Camera 1 using the MS Media Foundation API use `index = 1 + cv::CAP_MSMF`

    @sa The list of supported API backends cv::VideoCaptureAPIs
    */
    CV_WRAP VideoCapture(int index);

    /** @brief Default destructor

    The method first calls VideoCapture::release to close the already opened file or camera.
    */
    virtual ~VideoCapture();

    /** @brief  Open video file or a capturing device or a IP video stream for video capturing

    @overload

    Parameters are same as the constructor VideoCapture(const String& filename)
    @return `true` if the file has been successfully opened

    The method first calls VideoCapture::release to close the already opened file or camera.
     */
    CV_WRAP virtual bool open(const String& filename);

    /** @brief  Open a camera for video capturing

    @overload

    Parameters are same as the constructor VideoCapture(int index)
    @return `true` if the camera has been successfully opened.

    The method first calls VideoCapture::release to close the already opened file or camera.
    */
    CV_WRAP virtual bool open(int index);

   /** @brief  Open a camera for video capturing

    @overload

    Parameters are similar as the constructor VideoCapture(int index),except it takes an additional argument apiPreference.
    Definitely, is same as open(int index) where `index=cameraNum + apiPreference`
    @return `true` if the camera has been successfully opened.
    */
    CV_WRAP bool open(int cameraNum, int apiPreference);

    /** @brief Returns true if video capturing has been initialized already.

    If the previous call to VideoCapture constructor or VideoCapture::open() succeeded, the method returns
    true.
     */
    CV_WRAP virtual bool isOpened() const;

    /** @brief Closes video file or capturing device.

    The method is automatically called by subsequent VideoCapture::open and by VideoCapture
    destructor.

    The C function also deallocates memory and clears \*capture pointer.
     */
    CV_WRAP virtual void release();

    /** @brief Grabs the next frame from video file or capturing device.

    @return `true` (non-zero) in the case of success.

    The method/function grabs the next frame from video file or camera and returns true (non-zero) in
    the case of success.

    The primary use of the function is in multi-camera environments, especially when the cameras do not
    have hardware synchronization. That is, you call VideoCapture::grab() for each camera and after that
    call the slower method VideoCapture::retrieve() to decode and get frame from each camera. This way
    the overhead on demosaicing or motion jpeg decompression etc. is eliminated and the retrieved frames
    from different cameras will be closer in time.

    Also, when a connected camera is multi-head (for example, a stereo camera or a Kinect device), the
    correct way of retrieving data from it is to call VideoCapture::grab() first and then call
    VideoCapture::retrieve() one or more times with different values of the channel parameter.

    @ref tutorial_kinect_openni
     */
    CV_WRAP virtual bool grab();

    /** @brief Decodes and returns the grabbed video frame.

    @param [out] image the video frame is returned here. If no frames has been grabbed the image will be empty.
    @param flag it could be a frame index or a driver specific flag
    @return `false` if no frames has been grabbed

    The method decodes and returns the just grabbed frame. If no frames has been grabbed
    (camera has been disconnected, or there are no more frames in video file), the method returns false
    and the function returns an empty image (with %cv::Mat, test it with Mat::empty()).

    @sa read()

    @note In @ref videoio_c "C API", functions cvRetrieveFrame() and cv.RetrieveFrame() return image stored inside the video
    capturing structure. It is not allowed to modify or release the image! You can copy the frame using
    :ocvcvCloneImage and then do whatever you want with the copy.
     */
    CV_WRAP virtual bool retrieve(OutputArray image, int flag = 0);

    /** @brief Stream operator to read the next video frame.
    @sa read()
    */
    virtual VideoCapture& operator >> (CV_OUT Mat& image);

    /** @overload
    @sa read()
    */
    virtual VideoCapture& operator >> (CV_OUT UMat& image);

    /** @brief Grabs, decodes and returns the next video frame.

    @param [out] image the video frame is returned here. If no frames has been grabbed the image will be empty.
    @return `false` if no frames has been grabbed

    The method/function combines VideoCapture::grab() and VideoCapture::retrieve() in one call. This is the
    most convenient method for reading video files or capturing data from decode and returns the just
    grabbed frame. If no frames has been grabbed (camera has been disconnected, or there are no more
    frames in video file), the method returns false and the function returns empty image (with %cv::Mat, test it with Mat::empty()).

    @note In @ref videoio_c "C API", functions cvRetrieveFrame() and cv.RetrieveFrame() return image stored inside the video
    capturing structure. It is not allowed to modify or release the image! You can copy the frame using
    :ocvcvCloneImage and then do whatever you want with the copy.
     */
    CV_WRAP virtual bool read(OutputArray image);

    /** @brief Sets a property in the VideoCapture.

    @param propId Property identifier from cv::VideoCaptureProperties (eg. cv::CAP_PROP_POS_MSEC, cv::CAP_PROP_POS_FRAMES, ...)
    or one from @ref videoio_flags_others
    @param value Value of the property.
    @return `true` if the property is supported by backend used by the VideoCapture instance.
    @note Even if it returns `true` this doesn't ensure that the property
    value has been accepted by the capture device. See note in VideoCapture::get()
     */
    CV_WRAP virtual bool set(int propId, double value);

    /** @brief Returns the specified VideoCapture property

    @param propId Property identifier from cv::VideoCaptureProperties (eg. cv::CAP_PROP_POS_MSEC, cv::CAP_PROP_POS_FRAMES, ...)
    or one from @ref videoio_flags_others
    @return Value for the specified property. Value 0 is returned when querying a property that is
    not supported by the backend used by the VideoCapture instance.

    @note Reading / writing properties involves many layers. Some unexpected result might happens
    along this chain.
    @code {.txt}
    `VideoCapture -> API Backend -> Operating System -> Device Driver -> Device Hardware`
    @endcode
    The returned value might be different from what really used by the device or it could be encoded
    using device dependent rules (eg. steps or percentage). Effective behaviour depends from device
    driver and API Backend

    */
    CV_WRAP virtual double get(int propId) const;

    /** @brief Open video file or a capturing device or a IP video stream for video capturing with API Preference

    @overload

    Parameters are same as the constructor VideoCapture(const String& filename, int apiPreference)
    @return `true` if the file has been successfully opened

    The method first calls VideoCapture::release to close the already opened file or camera.
    */
    CV_WRAP virtual bool open(const String& filename, int apiPreference);

protected:
    Ptr<CvCapture> cap;
    Ptr<IVideoCapture> icap;
};

class IVideoWriter;

/** @example samples/cpp/tutorial_code/videoio/video-write/video-write.cpp
Check @ref tutorial_video_write "the corresponding tutorial" for more details
*/

/** @example samples/cpp/videowriter_basic.cpp
An example using VideoCapture and VideoWriter class
*/

/** @brief Video writer class.

The class provides C++ API for writing video files or image sequences.
*/
class CV_EXPORTS_W VideoWriter
{
public:
    /** @brief Default constructors

    The constructors/functions initialize video writers.
    -   On Linux FFMPEG is used to write videos;
    -   On Windows FFMPEG or VFW is used;
    -   On MacOSX QTKit is used.
     */
    CV_WRAP VideoWriter();

    /** @overload
    @param filename Name of the output video file.
    @param fourcc 4-character code of codec used to compress the frames. For example,
    VideoWriter::fourcc('P','I','M','1') is a MPEG-1 codec, VideoWriter::fourcc('M','J','P','G') is a
    motion-jpeg codec etc. List of codes can be obtained at [Video Codecs by
    FOURCC](http://www.fourcc.org/codecs.php) page. FFMPEG backend with MP4 container natively uses
    other values as fourcc code: see [ObjectType](http://www.mp4ra.org/codecs.html),
    so you may receive a warning message from OpenCV about fourcc code conversion.
    @param fps Framerate of the created video stream.
    @param frameSize Size of the video frames.
    @param isColor If it is not zero, the encoder will expect and encode color frames, otherwise it
    will work with grayscale frames (the flag is currently supported on Windows only).

    @b Tips:
    - With some backends `fourcc=-1` pops up the codec selection dialog from the system.
    - To save image sequence use a proper filename (eg. `img_%02d.jpg`) and `fourcc=0`
      OR `fps=0`. Use uncompressed image format (eg. `img_%02d.BMP`) to save raw frames.
    - Most codecs are lossy. If you want lossless video file you need to use a lossless codecs
      (eg. FFMPEG FFV1, Huffman HFYU, Lagarith LAGS, etc...)
    - If FFMPEG is enabled, using `codec=0; fps=0;` you can create an uncompressed (raw) video file.
    */
    CV_WRAP VideoWriter(const String& filename, int fourcc, double fps,
                Size frameSize, bool isColor = true);

    /** @overload
    The `apiPreference` parameter allows to specify API backends to use. Can be used to enforce a specific reader implementation
    if multiple are available: e.g. cv::CAP_FFMPEG or cv::CAP_GSTREAMER.
     */
    CV_WRAP VideoWriter(const String& filename, int apiPreference, int fourcc, double fps,
                Size frameSize, bool isColor = true);

    /** @brief Default destructor

    The method first calls VideoWriter::release to close the already opened file.
    */
    virtual ~VideoWriter();

    /** @brief Initializes or reinitializes video writer.

    The method opens video writer. Parameters are the same as in the constructor
    VideoWriter::VideoWriter.
    @return `true` if video writer has been successfully initialized

    The method first calls VideoWriter::release to close the already opened file.
     */
    CV_WRAP virtual bool open(const String& filename, int fourcc, double fps,
                      Size frameSize, bool isColor = true);

    /** @overload
     */
    CV_WRAP bool open(const String& filename, int apiPreference, int fourcc, double fps,
                      Size frameSize, bool isColor = true);

    /** @brief Returns true if video writer has been successfully initialized.
    */
    CV_WRAP virtual bool isOpened() const;

    /** @brief Closes the video writer.

    The method is automatically called by subsequent VideoWriter::open and by the VideoWriter
    destructor.
     */
    CV_WRAP virtual void release();

    /** @brief Stream operator to write the next video frame.
    @sa write
    */
    virtual VideoWriter& operator << (const Mat& image);

    /** @brief Writes the next video frame

    @param image The written frame. In general, color images are expected in BGR format.

    The function/method writes the specified image to video file. It must have the same size as has
    been specified when opening the video writer.
     */
    CV_WRAP virtual void write(const Mat& image);

    /** @brief Sets a property in the VideoWriter.

     @param propId Property identifier from cv::VideoWriterProperties (eg. cv::VIDEOWRITER_PROP_QUALITY)
     or one of @ref videoio_flags_others

     @param value Value of the property.
     @return  `true` if the property is supported by the backend used by the VideoWriter instance.
     */
    CV_WRAP virtual bool set(int propId, double value);

    /** @brief Returns the specified VideoWriter property

     @param propId Property identifier from cv::VideoWriterProperties (eg. cv::VIDEOWRITER_PROP_QUALITY)
     or one of @ref videoio_flags_others

     @return Value for the specified property. Value 0 is returned when querying a property that is
     not supported by the backend used by the VideoWriter instance.
     */
    CV_WRAP virtual double get(int propId) const;

    /** @brief Concatenates 4 chars to a fourcc code

    @return a fourcc code

    This static method constructs the fourcc code of the codec to be used in the constructor
    VideoWriter::VideoWriter or VideoWriter::open.
     */
    CV_WRAP static int fourcc(char c1, char c2, char c3, char c4);

protected:
    Ptr<CvVideoWriter> writer;
    Ptr<IVideoWriter> iwriter;

    static Ptr<IVideoWriter> create(const String& filename, int fourcc, double fps,
                                    Size frameSize, bool isColor = true);
};

template<> CV_EXPORTS void DefaultDeleter<CvCapture>::operator ()(CvCapture* obj) const;
template<> CV_EXPORTS void DefaultDeleter<CvVideoWriter>::operator ()(CvVideoWriter* obj) const;

//! @} videoio

} // cv

#endif //OPENCV_VIDEOIO_HPP
