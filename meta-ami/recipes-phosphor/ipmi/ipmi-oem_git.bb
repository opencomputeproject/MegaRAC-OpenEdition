SUMMARY = "Intel OEM IPMI commands"
DESCRIPTION = "Intel OEM IPMI commands"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a6a4edad4aed50f39a66d098d74b265b"

SRC_URI = "git://github.com/openbmc/intel-ipmi-oem.git"
SRCREV = "b910987a7d832e38e9342f0946aeb555a48f9cb0"

S = "${WORKDIR}/git"
PV = "0.1+git${SRCPV}"

DEPENDS = "boost phosphor-ipmi-host phosphor-logging systemd intel-dbus-interfaces libgpiod"

inherit cmake obmc-phosphor-ipmiprovider-symlink

EXTRA_OECMAKE="-DENABLE_TEST=0 -DYOCTO=1"
EXTRA_OECMAKE += "${@bb.utils.contains('IMAGE_FSTYPES', 'intel-pfr', '-DINTEL_PFR_ENABLED=ON', '', d)}"
EXTRA_OECMAKE += "${@bb.utils.contains('EXTRA_IMAGE_FEATURES', 'validation-unsecure', '-DBMC_VALIDATION_UNSECURE_FEATURE=ON', '', d)}"
EXTRA_OECMAKE += "-DUSING_ENTITY_MANAGER_DECORATORS=OFF"

LIBRARY_NAMES = "libzinteloemcmds.so"

HOSTIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"
NETIPMI_PROVIDER_LIBRARY += "${LIBRARY_NAMES}"

FILES_${PN}_append = " ${libdir}/ipmid-providers/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/host-ipmid/lib*${SOLIBS}"
FILES_${PN}_append = " ${libdir}/net-ipmid/lib*${SOLIBS}"
FILES_${PN}-dev_append = " ${libdir}/ipmid-providers/lib*${SOLIBSDEV}"

do_install_append(){
   install -d ${D}${includedir}/intel-ipmi-oem
   install -m 0644 -D ${S}/include/*.hpp ${D}${includedir}/intel-ipmi-oem
}
