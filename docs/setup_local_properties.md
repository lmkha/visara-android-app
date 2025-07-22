2. Add **local.properties** file:
```env

## This file must *NOT* be checked into Version Control Systems,
# as it contains information specific to your local configuration.
#
# Location of the SDK. This is only used by Gradle.
# For customization when using a Version Control System, please read the
# header note.
sdk.dir=E\:\\AndroidSdk
## Backend server
BACKEND_SCHEME_DEBUG=http
BACKEND_HOST_DEBUG=10.0.2.2
BACKEND_PORT_DEBUG=8080

# Real server (release)
BACKEND_SCHEME_RELEASE=https
BACKEND_HOST_RELEASE=api.visara.com
BACKEND_PORT_RELEASE=443

API_VERSION=v1

# Deep Linking Configuration
# App Link scheme (must be https)
DEEPLINK_SCHEME=https
# Common host (for both custom + app link)
DEEPLINK_HOST=visara.com

```