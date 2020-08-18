#! /bin/sh
# Copyright (C) 2019 Intel
# MIT licensed

# TODO: lots of hardcoded values in here.  Need a generic solution.

set -e

# Prune previous ACRN boot entries
for boot in $(efibootmgr | perl -n -e '/Boot([0-9a-fA-F]+).*ACRN.*/ && print "$1\n"'); do
    efibootmgr -b $boot -B
done

# Where did we boot from?
# Where is our ESP?
if [ -d /dev/disk/by-partlabel/msdos ]; then
    # standard partitions
    msdos=$(ls -la /dev/disk/by-partlabel/msdos)
    platform=$(ls -la /dev/disk/by-partlabel/platform)
elif [ -d /dev/disk/by-partlabel/primary_uefi ]; then
# acrn-image-verified-boot partitions 
    primary_uefi=$(ls -la /dev/disk/by-partlabel/primary_uefi)
    secondary_uefi=$(ls -la /dev/disk/by-partlabel/secondary_uefi)
    rootfs=$(ls -la /dev/disk/by-partlabel/rootfs)
fi

efibootmgr -c -l "\EFI\BOOT\acrn.efi" \
    -L "ACRN (Yocto)" \
    -d /dev/sdb \
    -p 1 \
    -u "bootloader=\EFI\BOOT\bootx64.efi uart=port@0x3f8 "
