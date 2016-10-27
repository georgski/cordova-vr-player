#import <Cordova/CDVPlugin.h>
#import "VideoPlayerViewController.h"

@interface GoogleVRPlayer : CDVPlugin {
}

// The hooks for our plugin commands
- (void)playVideo:(CDVInvokedUrlCommand *)command;

@property (readwrite, assign) BOOL hasPendingOperation;

@end
