#import "GoogleVRPlayer.h"

#import <Cordova/CDVAvailability.h>

@implementation GoogleVRPlayer

- (void)pluginInitialize {
}

- (void)playVideo:(CDVInvokedUrlCommand *)command {
    NSString * videoUrl = [command.arguments objectAtIndex:0];
    
    // Set the hasPendingOperation field to prevent the webview from crashing
    self.hasPendingOperation = YES;
        
    // Launch the storyboard
    UIStoryboard *sb = [UIStoryboard storyboardWithName:@"Main" bundle:nil];
    UIViewController *vc = [sb instantiateViewControllerWithIdentifier:@"videoBoardId"];
    
    [vc setValue:videoUrl forKey:@"videoUrl"];
    
    [self.viewController presentViewController:vc animated:YES completion:NULL];

}

@end
