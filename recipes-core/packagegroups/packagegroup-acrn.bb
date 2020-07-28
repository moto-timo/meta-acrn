SUMMARY = "ACRN hypervisor"

inherit packagegroup features_check

# Currently requires systemd as the networking glue is systemd-specific
REQUIRED_DISTRO_FEATURES = "systemd"

RDEPENDS_${PN} = "\
    acrn-hypervisor \
    acrn-tools \
    acrn-devicemodel \
    "

# For dm-verity, we need a read-only-rootfs and cannot include acrn-efi-setup, as it performs a post install task
# When we are building packagegroup-acrn, we are not in an IMAGE_FEATURES context, where read-only-rootfs would
# be assigned, so we need to use something more global: DISTRO_FEATURES
RDEPENDS_${PN}_append = " ${@ bb.utils.contains('DISTRO_FEATURES', 'dm-verity', '', 'acrn-efi-setup', d)} "

# Add debug symbols
PACKAGE_INSTALL_append = " ${@ bb.utils.contains('DISTRO_FEATURES', 'acrn-debug', 'acrn-hypervisor-dbg', '', d)}"
