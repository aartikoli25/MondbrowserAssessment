# MondbrowserAssessment

# This is an Assessment Android Application README.

# Installation
# Clone this repository and import into Android Studio

# git clone https://github.com/aartikoli25/MondbrowserAssessment.git
# Configuration
# Keystores:
# Create app/keystore.gradle with the following info:

# ext.key_alias='...'
# ext.key_password='...'
# ext.store_password='...'
# And place both keystores under app/keystores/ directory:

# playstore.keystore
# stage.keystore
# Build variants
# Use the Android Studio Build Variants button to choose between production and staging flavors combined with debug and release build types

# Generating signed APK
# From Android Studio:

# Build menu
# Generate Signed APK...
# Fill in the keystore information (you only need to do this once manually and then let Android Studio remember it)

# Maintainers
# This project is mantained by:
# Aarti Koli

# Contributing
# Fork it
# Create your feature branch (git checkout -b my-new-feature)
# Commit your changes (git commit -m 'Add some feature')
# Run the linter (ruby lint.rb').
# Push your branch (git push origin my-new-feature)
# Create a new Pull Request