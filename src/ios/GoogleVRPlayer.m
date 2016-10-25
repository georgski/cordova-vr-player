#import "GoogleVRPlayer.h"

#import <Cordova/CDVAvailability.h>

@implementation GoogleVRPlayer

- (void)pluginInitialize {
}

- (void)playVideo:(CDVInvokedUrlCommand *)command {
  NSString* videoUrl = [command.arguments objectAtIndex:0];
  NSLog(@"%@", videoUrl);
}

@end
