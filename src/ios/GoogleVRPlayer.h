#import <Cordova/CDVPlugin.h>

@interface GoogleVRPlayer : CDVPlugin {
}

// The hooks for our plugin commands
- (void)playVideo:(CDVInvokedUrlCommand *)command;

@end
