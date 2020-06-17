# The uefi-sign.bbclass in meta-intel assumes an image recipe, since it is
# checking for 'secureboot' in IMAGE_FEATURES. For acrn.efi, we actually deploy
# to the tmp/deploy/images directory in this recipe.
# We reuse the same signing key and cert that will also be signing the normal efi bootloader uefi-sign.bbclass
# By default, sign the .efi binary in ${D}/usr/lib/acrn after do_install and before do_deploy
ACRN_SIGNING_DIR ?= "${D}${libdir}/acrn"
ACRN_SIGNING_BINARIES ?= "acrn.${ACRN_BOARD}.${ACRN_SCENARIO}.efi"
ACRN_SIGN_AFTER ?= "do_install"
ACRN_SIGN_BEFORE ?= "do_deploy"

python () {
    import os
    import hashlib

    if bb.utils.contains('DISTRO_FEATURES', 'secureboot', True, False, d):
        # Ensure that if the signing key or cert change, we rerun the uefiapp process
        for varname in ('SECURE_BOOT_SIGNING_CERT', 'SECURE_BOOT_SIGNING_KEY'):
            filename = d.getVar(varname)
            if filename is None:
                bb.fatal('%s is not set.' % varname)
            if not os.path.isfile(filename):
                bb.fatal('%s=%s is not a file.' % (varname, filename))
            with open(filename, 'rb') as f:
                data = f.read()
            hash = hashlib.sha256(data).hexdigest()
            d.setVar('%s_HASH' % varname, hash)

            # Must reparse and thus rehash on file changes.
            bb.parse.mark_dependency(d, filename)

        bb.build.addtask('acrn_uefi_sign', d.getVar('ACRN_SIGN_BEFORE'), d.getVar('ACRN_SIGN_AFTER'), d)

        # Original binary needs to be regenerated if the hash changes since we overwrite it
        # SIGN_AFTER isn't necessarily when it gets generated, but its our best guess
        d.appendVarFlag(d.getVar('ACRN_SIGN_AFTER'), 'vardeps', 'SECURE_BOOT_SIGNING_CERT_HASH SECURE_BOOT_SIGNING_KEY_HASH')
    else:
        bb.debug(2, "Skipping do_acrn_uefi_sign as 'secureboot' is not in DISTRO_FEATURES.You can add the following to e.g. local.conf:\nDISTRO_FEATURES_ACRN_SOS = \"secureboot\"")
}

do_acrn_uefi_sign() {
    if [ -f ${SECURE_BOOT_SIGNING_KEY} ] && [ -f ${SECURE_BOOT_SIGNING_CERT} ]; then
        for i in `find ${ACRN_SIGNING_DIR}/ -name '${ACRN_SIGNING_BINARIES}'`; do
            echo 'ACRN uefi signing: '$i
            sbsign --key ${SECURE_BOOT_SIGNING_KEY} --cert ${SECURE_BOOT_SIGNING_CERT} $i
            sbverify --cert ${SECURE_BOOT_SIGNING_CERT} $i.signed
            mv $i.signed $i
        done
    fi
}

do_acrn_uefi_sign[depends] += "sbsigntool-native:do_populate_sysroot"

do_acrn_uefi_sign[vardeps] += "SECURE_BOOT_SIGNING_CERT_HASH \
                          SECURE_BOOT_SIGNING_KEY_HASH  \
                          ACRN_SIGNING_BINARIES ACRN_SIGNING_DIR  \
                          ACRN_SIGN_BEFORE ACRN_SIGN_AFTER        \
                         "
