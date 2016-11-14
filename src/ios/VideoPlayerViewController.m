#import <UIKit/UIKit.h>

#import "VideoPlayerViewController.h"
#import "GoogleVRPlayer.h"
#import "GVRVideoView.h"

@interface VideoPlayerViewController () <GVRVideoViewDelegate>
@property (unsafe_unretained, nonatomic) IBOutlet GVRVideoView *videoView;
@end

@implementation VideoPlayerViewController

- (instancetype)init {
    self = [super initWithNibName:nil bundle:nil];
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _videoView.delegate = self;
    _videoView.enableFullscreenButton = YES;
    _videoView.enableCardboardButton = YES;
    _videoView.enableTouchTracking = YES;
    _videoView.displayMode = kGVRWidgetDisplayModeFullscreen;
    
    self.fallbackVideoPlayed = NO;
    
    NSString *videoPath  =  [self valueForKey:@"videoUrl"];
    
    NSURL *videoUrl  = [NSURL URLWithString:videoPath];
    
    [_videoView loadFromUrl:videoUrl
                     ofType:kGVRVideoTypeMono];
}

#pragma mark - GVRVideoViewDelegate

- (void)widgetView:(GVRWidgetView *)widgetView didLoadContent:(id)content {
    NSLog(@"Finished loading video");
}

- (void)widgetView:(GVRWidgetView *)widgetView
didChangeDisplayMode:(GVRWidgetDisplayMode)displayMode{
    if (displayMode != kGVRWidgetDisplayModeFullscreen && displayMode != kGVRWidgetDisplayModeFullscreenVR){
        // Full screen closed, closing the view
        [self dismissViewControllerAnimated:YES completion:nil];
    }
}

- (void)widgetView:(GVRWidgetView *)widgetView
didFailToLoadContent:(id)content
  withErrorMessage:(NSString *)errorMessage {
    
    NSLog(@"Failed to load video: %@", errorMessage);
    NSString *videoPath  =  [self valueForKey:@"fallbackVideoUrl"];
    
    if (!([videoPath isEqual:[NSNull null]]) && ([errorMessage isEqualToString:@"Cannot Decode"] || self.fallbackVideoPlayed == NO)){
        self.fallbackVideoPlayed = YES;
        
        NSURL *videoUrl  = [NSURL URLWithString:videoPath];
        [_videoView loadFromUrl:videoUrl
                         ofType:kGVRVideoTypeMono];
    }else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Failed to load video"
                                                        message:errorMessage
                                                       delegate:nil
                                              cancelButtonTitle:@"OK"
                                              otherButtonTitles:nil];
        [alert show];
    }
}

- (void)videoView:(GVRVideoView*)videoView didUpdatePosition:(NSTimeInterval)position {
    // Loop the video when it reaches the end.
    if (position == videoView.duration) {
        [_videoView seekTo:0];
        [_videoView resume];
    }
}

@end
