# We cannot require core-image-base, as it is incompatible with adding
# our secureboot or dm-verity features. Instead, use the meaningful bits
# of core-image-base and acrn-image-base here
SUMMARY = "A console-only image that fully supports the target device \
hardware, optionally with secureboot and dm-verity."

LICENSE = "MIT"

inherit core-image

CORE_IMAGE_EXTRA_INSTALL_append = " \
    packagegroup-acrn \
    linux-firmware \
    kernel-modules \
"
CORE_IMAGE_EXTRA_INSTALL_remove = "acrn-efi-setup"

inherit image-acrn

INITRD_IMAGE_intel-corei7-64 = "acrn-initramfs"

# Set in DISTRO_EXTRA_FEATURES_SOS or DISTRO_EXTRA_FEATURES_UOS as appropriate
IMAGE_FEATURES_append = " \
    ${@ bb.utils.contains('DISTRO_FEATURES', 'dm-verity', 'dm-verity read-only-rootfs', '', d)}  \
    ${@ bb.utils.contains('DISTRO_FEATURES', 'secureboot', 'secureboot', '', d)} \
"

# By default, the full image is meant to fit into 4*10^9 bytes, i.e.
# "4GB" regardless whether 1000 or 1024 is used as base. 64M are reserved
# for potential partitioning overhead.
WKS_FILE ?= "acrn-directdisk.wks.in"
# We need no boot loaders and only a few of the default native tools.
WKS_FILE_DEPENDS = "e2fsprogs-native"
ACRN_VFAT_MB ??= "64"
ACRN_IMAGE_SIZE ??= "--fixed-size 3622M"
ACRN_EXTRA_PARTITION ??= ""
WIC_CREATE_EXTRA_ARGS += " -D"

# Image creation: add here the desired value for the PARTUUID of
# the rootfs. WARNING: any change to this value will trigger a
# rebuild (and re-sign, if enabled) of the combo EFI application.
REMOVABLE_MEDIA_ROOTFS_PARTUUID_VALUE = "12345678-9abc-def0-0fed-cba987654321"
# turned into root=PARTUUID=... by uefi-comboapp.bbclass
DISK_SIGNATURE_UUID = "${REMOVABLE_MEDIA_ROOTFS_PARTUUID_VALUE}"
# The second value is needed for the system installed onto
# the device's internal storage in order to mount correct rootfs
# when an installation media is still inserted into the device.
#INT_STORAGE_ROOTFS_PARTUUID_VALUE = "12345678-9abc-def0-0fed-cba987654320"

# When using dm-verity, the rootfs has to be read-only.
# An extra partition gets created by wic which holds the
# hash data for the rootfs partition, including a signed
# root hash.
#
# A suitable initramfs (like acrn-initramfs with the dm-verity
# image feature enabled) then validates the signed root
# hash and activates the rootfs. acrn-initramfs checks the
# "dmverity" boot parameter for that. If not present or
# the acrn-initramfs was built without dm-verity support,
# booting proceeds without integrity protection.
WKS_FILE_DEPENDS_append = " \
    ${@ bb.utils.contains('IMAGE_FEATURES', 'dm-verity', 'cryptsetup-native openssl-native', '', d)} \
"
ACRN_DM_VERITY_PARTUUID = "12345678-9abc-def0-0fed-cba987654322"
ACRN_DM_VERITY_PARTITION () {
part --source dm-verity --uuid ${ACRN_DM_VERITY_PARTUUID} --label rootfs
}
ACRN_EXTRA_PARTITION .= "${@ bb.utils.contains('IMAGE_FEATURES', 'dm-verity', d.getVar('ACRN_DM_VERITY_PARTITION'), '', d) }"
APPEND_append = "${@ bb.utils.contains('IMAGE_FEATURES', 'dm-verity', ' dmverity=PARTUUID=${ACRN_DM_VERITY_PARTUUID}', '', d) }"
WICVARS_append = "${@ bb.utils.contains('IMAGE_FEATURES', 'dm-verity', ' \
    ACRN_DMVERITY_PRIVATE_KEY \
    ACRN_DMVERITY_PRIVATE_KEY_HASH \
    ACRN_DMVERITY_PASSWORD \
    ', '', d) } \
"
inherit acrn-hash-dm-verity-key

# Here is the complete list of image features, also including
# those that modify the image configuration.
IMAGE_FEATURES[validitems] += " \
    secureboot \
    dm-verity \
"
