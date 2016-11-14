#import <UIKit/UIKit.h>

@interface VideoPlayerViewController : UIViewController

@property (nonatomic, retain) NSString* videoUrl;
@property (nonatomic, retain) NSString* fallbackVideoUrl;
@property (nonatomic, assign) BOOL fallbackVideoPlayed;

@end
