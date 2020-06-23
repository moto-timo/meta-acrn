# Typically, generic BSPs do not enable special features that are
# needed only by some distros. For example, TPM, dm-verity and
# nf-tables are disabled in meta-intel BSPs.
#
# But acrn may want certain kernel features enabled depending on distro
# features. Expecting the developer to know about this and then make
# changes to the BSP they are using is not very developer-friendly and
# also makes automated testing in the CI harder.
#
# Therefore we use this .bbappend to reconfigure all kernel recipes
# called linux-<something>-sos. Using a .bbappend instead of manipulating
# SRC_URI in acrn-config.inc is a performance tweak: this way
# we avoid touching the SRC_URI of all recipes. The same code would
# also work in global scope.
#
# Since we may want different DISTRO_FEATURES for SOS vs UOS,
# utilize the DISTRO_FEATURES_ACRN_SOS and DISTRO_FEATURES_ACRN_UOS
# variables in local.conf (or other global conf) to differentiate.
#
# Reconfiguring works for kernel recipes that support kernel config
# fragments. If the default is undesired, then override or modify
# ACRN_KERNEL_SRC_URI for the kernel recipe(s) that this bbappend is
# not meant to modify.
KERNEL_EXTRA_FEATURES_ACRN ??= "${@ acrn_kernel_config(d) }"

# Originally, the code from refkit had local config fragments,
# but the featues we need are in upstream yocto-kernel-cache
# So rather than adding  to SRC_URI, we add to KERNEL_FEATURES
def acrn_kernel_config(d):
    bb.debug(1, 'acrn linux-.bbappend: inherit kernel? %s' % bb.data.inherits_class('kernel',d))
    # This maps distro features to the corresponding feature definition file(s).
    distro2config = {
        'dm-verity': 'features/device-mapper/dm-verity.scc',
        'tpm': 'features/tpm/tpm.scc',
        'acrn-firewall': 'features/nf_tables/nf_tables.scc',
    }
    k_features = []
    for feature in d.getVar('DISTRO_FEATURES').split():
        bb.debug(1, 'acrn_kernel_config: DISTRO_FEATURES: feature: %s' % feature)
        config = distro2config.get(feature, None)
        if config:
            k_features.extend([x for x in config.split()])
    return ' '.join(k_features)

KERNEL_EXTRA_FEATURES_append = " \
    ${@ d.getVar('KERNEL_EXTRA_FEATURES_ACRN') if \
        bb.data.inherits_class('kernel', d) \
        else '' } \
"
