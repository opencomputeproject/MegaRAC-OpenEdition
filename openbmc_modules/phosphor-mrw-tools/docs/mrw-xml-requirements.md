# OpenBMC MRW Requirements

This document describes the data requirements that OpenBMC has on the machine
readable workbook XML generated by [Serverwiz2](https://www.github.com/open-power/serverwiz).
The requirements in this document are broken up by the OpenBMC functions that
use them.

If a particular OpenBMC implementation doesn't use a certain function,
then that MRW data isn't required.

## System Inventory

The system inventory can be generated from the MRW XML.  The inventory
typically contains all FRUs (field replaceable units), along with a few
non-FRU entities, like the BMC chip and processor cores.

To specify a target in the MRW should be in the inventory:

* Set the FRU\_NAME attribute of that target.

**Note**: The BMC and cores will be automatically added without the need to
set FRU\_NAME.


## BMC Device Tree

The BMC device tree can be generated from the MRW XML.  For the full device
tree to be generated, all of the corresponding devices and connections must
be modeled in the MRW XML.  For a system built with parts that already have
existing XML representations, there are only a few attributes that need to
be set.  If a new part is being modeled, initial values for some attributes
may need to be determined, depending on the part.

The following sections list the system dependent information that the device
tree generator requires.  The majority of the information it requires is
static data that is either already built into the XML representations of
the existing parts or pulled in from an include file.


### System Level Attributes

##### SYSTEM\_NAME
The name of the system as the firmware would know it.

##### MANUFACTURER
The system manufacturer.


### BMC Chip
All of the BMC chip attributes that are needed for the device tree are
pre-built into the XML representation and don't need to be updated when
the device is placed into a system in Serverwiz.

> Note:  The AST2500 is currently the only BMC XML model that contains all
of the necessary device tree attributes.


### BMC SPI Flashes
The device tree can support either 1 or 2 BMC SPI flash chips.  This is
accomplished by connecting instances of the `BMC_FLASH` part to the
spi-master-unit on the BMC that has its `SPI_FUNCTION` attribute set to
`BMC_CODE`.  If there are multiple chips, they are both connected to the
same unit.


### Ethernet MAC Units
To enable a BMC MAC module, its ethernet master unit in the MRW must be
connected to an ethernet slave unit.  Additionally, the following
attributes may need to be set.

##### NCSI\_MODE
This attribute in the ethernet master unit can be set to 1 if the link uses
NCSI.  The default is 0.

##### USE\_HW\_CHECKSUM
This attribute in the ethernet master unit can be set to 1 if the MAC has
hardware checksum enabled, or 0 if not enabled.  The default is 1.


### UARTS
UARTs are enabled by connecting the appropriate UART master units in the
BMC part to their corresponding uart slave units.  No additional attributes
are required.


### LEDs
LEDS will be listed in the device tree when LED parts in the MRW are wired to
their GPIO master endpoints.  The instance name in the MRW is the name of
its node in the device tree.

##### ON\_STATE
Set to the logic value required to activate the LED - either 0 or 1.  The
default is 0.


### I2C
I2C devices are enabled by connecting the I2C master units in the BMC to
the I2C slave units on the devices.

##### I2C\_ADDRESS
The 8 bit hexadecimal I2C address should be set in the slave unit of the
end device.

##### BMC\_DT\_COMPATIBLE
When creating a new XML device model, this attribute should be used to
specify which device driver the kernel will use to access the device.
For example, `ti,423` or `bosch,bmp280`.  For existing parts,  this should
already be set.

##### BMC\_DT\_ATTR\_NAMES
This attribute is also only required when creating a new XML representation
of an I2C device.  It specifies which other attributes of the device should be
listed as properties in the device tree, as required by the device driver
for that device. It can contain up to 4 pairs of names, the first name in the
pair is the attribute name in the XML to read, and the second name in the
pair is the name of the property to call it in the device tree.  For example,
`ATTR_WRITE_PAGE_SIZE, pagesize` indicates that the value of the
`ATTR_WRITE_PAGE_SIZE` attribute should be stored in a property called
`pagesize` in the device tree.
