SUMMARY = "Linux Kernel 5.4 with ACRN enabled"

require recipes-kernel/linux/linux-intel.inc

SRC_URI_append = "  file://0001-menuconfig-mconf-cfg-Allow-specification-of-ncurses-.patch"

LIC_FILES_CHKSUM = "file://COPYING;md5=bbea815ee2795b2f4230826c0c6b8814"

KBRANCH = "5.4/yocto"
KMETA_BRANCH = "yocto-5.4"

LINUX_VERSION ?= "5.4.51"
SRCREV_machine ?= "b8b4b73e4f7bffe85610b37cef411bf55b60fbe4"
SRCREV_meta ?= "caafbdfe382bf22a4786d871af097acd49d0867a"

DEPENDS += "elfutils-native openssl-native util-linux-native"

KERNEL_FEATURES_append = "features/netfilter/netfilter.scc \
                          features/security/security.scc  \
                          cfg/hv-guest.scc \
                          cfg/paravirt_kvm.scc \
"

# Kernel config 'CONFIG_GPIO_LYNXPOINT' goes by a different name 'CONFIG_PINCTRL_LYNXPOINT' in
# linux-intel 5.4 specifically. This causes a warning during kernel config audit. So suppress the
# harmless warning for now.
KCONF_BSP_AUDIT_LEVEL = "0"
