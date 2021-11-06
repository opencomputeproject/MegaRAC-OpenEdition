SUMMARY = "System call tracing tool"
HOMEPAGE = "http://strace.io"
SECTION = "console/utils"
LICENSE = "LGPL-2.1+ & GPL-2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=c756d9d5dabc27663df64f0bf492166c"

SRC_URI = "https://strace.io/files/${PV}/strace-${PV}.tar.xz \
           file://disable-git-version-gen.patch \
           file://update-gawk-paths.patch \
           file://Makefile-ptest.patch \
           file://run-ptest \
           file://mips-SIGEMT.patch \
           file://0001-caps-abbrev.awk-fix-gawk-s-path.patch \
           file://ptest-spacesave.patch \
           file://uintptr_t.patch \
           file://0001-strace-fix-reproducibilty-issues.patch \
           file://0001-xlat-Mark-IPPROTO_MAX-last-in-IPPROTO_-constants.patch \
           "
SRC_URI[sha256sum] = "df4a669f7fff9cc302784085bd4b72fab216a426a3f72c892b28a537b71e7aa9"

inherit autotools ptest

PACKAGECONFIG_class-target ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} \
"

PACKAGECONFIG[bluez] = "ac_cv_header_bluetooth_bluetooth_h=yes,ac_cv_header_bluetooth_bluetooth_h=no,bluez5"
PACKAGECONFIG[libunwind] = "--with-libunwind,--without-libunwind,libunwind"

EXTRA_OECONF += "--enable-mpers=no --disable-gcc-Werror"

CFLAGS_append_libc-musl = " -Dsigcontext_struct=sigcontext"

TESTDIR = "tests"
PTEST_BUILD_HOST_PATTERN = "^(DEB_CHANGELOGTIME|RPM_CHANGELOGTIME|WARN_CFLAGS_FOR_BUILD|LDFLAGS_FOR_BUILD)"

do_install_append() {
	# We don't ship strace-graph here because it needs perl
	rm ${D}${bindir}/strace-graph
}

do_compile_ptest() {
	oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
	oe_runmake -C ${TESTDIR} install-ptest BUILDDIR=${B} DESTDIR=${D}${PTEST_PATH} TESTDIR=${TESTDIR}
	install -m 755 ${S}/test-driver ${D}${PTEST_PATH}
	install -m 644 ${B}/config.h ${D}${PTEST_PATH}
        sed -i -e '/^src/s/strace.*[1-9]/ptest/' ${D}/${PTEST_PATH}/${TESTDIR}/Makefile
}

RDEPENDS_${PN}-ptest += "make coreutils grep gawk sed"

BBCLASSEXTEND = "native"
TOOLCHAIN = "gcc"
