require recipes-graphics/images/core-image-weston.bb

CORE_IMAGE_EXTRA_INSTALL_append = " \
    packagegroup-acrn \
    linux-firmware \
    kernel-modules \
"

inherit image-acrn

# Set in DISTRO_EXTRA_FEATURES_SOS or DISTRO_EXTRA_FEATURES_UOS as appropriate
IMAGE_FEATURES_append = " \
    ${@ bb.utils.contains('DISTRO_FEATURES', 'secureboot', 'secureboot', '', d)} \
"

# Here is the complete list of image features, also including
# those that modify the image configuration.
IMAGE_FEATURES[validitems] += " \
    secureboot \
"
