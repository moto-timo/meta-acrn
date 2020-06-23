SUMMARY = "ACRN Demo initramfs image"
DESCRIPTION = "Finds the boot partition via PARTUUID, optionally supports dm-verity."
# IMA and LUKS could be added from intel-iot-refkit, if ACRN supports it.
# OSTree could also be implemented using intel-iot-refkit as a guide

PACKAGE_INSTALL = "busybox base-passwd ${ROOTFS_BOOTSTRAP_INSTALL} ${FEATURE_INSTALL}"

# Do not build by default, some relevant settings (like encryption keys)
# might be missing. If it is needed, it will get pulled in indirectly.
EXCLUDE_FROM_WORLD = "1"

# e2fs: loads fs modules and adds ext2/ext3/ext4=<device>:<path> boot parameter
#       for mounting additional partitions

# used to detect boot devices automatically
PACKAGE_INSTALL += "initramfs-module-udev"

# Do not pollute the initrd image with rootfs features
IMAGE_FEATURES = ""

# Instead we have additional image feature(s).
IMAGE_FEATURES[validitems] += " \
    dm-verity \
    debug \
"

IMAGE_FEATURES += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'dm-verity', '', d)} \
"
FEATURE_PACKAGES_dm-verity = "initramfs-framework-acrn-dm-verity openssl-bin"

FEATURE_PACKAGES_debug = "initramfs-module-debug"

IMAGE_LINGUAS = ""

LICENSE = "MIT"

IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"
inherit core-image

BAD_RECOMMENDATIONS += "busybox-syslog"
