# Determines what name is used under /dev/mapper/ for the rootfs.
ACRN_DEVICE_MAPPER_ROOTFS_NAME ??= "rootfs"

# Use the pre-generated OpenSSL keys for dm-verity signing with
# well-known password "acrndemo" passed directly via the command line.
# Other openssl -passin variants (for example, file:<path to password file>)
# also work.
ACRN_DMVERITY_PRIVATE_KEY ??= "${META_ACRN_CORE_BASE}/files/dm-verity/private.pem"
ACRN_DMVERITY_PASSWORD ??= "pass:acrndemo"

# Pre-generated test/development keys for UEFI combo application signing
SECURE_BOOT_SIGNING_KEY ??= "${META_ACRN_CORE_BASE}/files/secureboot/acrn-db.key"
SECURE_BOOT_SIGNING_CERT ??= "${META_ACRN_CORE_BASE}/files/secureboot/acrn-db.crt"
