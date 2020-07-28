require recipes-core/images/core-image-base.bb

CORE_IMAGE_EXTRA_INSTALL_append = " \
    packagegroup-acrn \
    linux-firmware \
    kernel-modules \
"

inherit image-acrn

# Set in DISTRO_EXTRA_FEATURES_SOS
IMAGE_FEATURES_append = " \
    ${@ bb.utils.contains('DISTRO_FEATURES', 'secureboot', 'secureboot', '', d)} \
    ${@ bb.utils.contains('DISTRO_FEATURES', 'acrn-debug', 'acrn-debug', '', d)} \
"

INHERIT_append = " ${@ bb.utils.contains('DISTRO_FEATURES', 'secureboot', 'uefi-comboapp', '', d)}"

# Here is the complete list of image features, also including
# those that modify the image configuration.
IMAGE_FEATURES[validitems] += " \
    secureboot \
    acrn-debug \
"
