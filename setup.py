import os.path, sys

from setuptools import setup

from scienceworld import scienceworld
from scienceworld.utils import DEFAULT_JAR_PATH

OBJECTS_LUT_FILE = "object_type_ids.tsv"
VERSION = scienceworld.version.__version__

JAR_FILE = DEFAULT_JAR_PATH.name

if not DEFAULT_JAR_PATH.exists():
    print('ERROR: Unable to find required library:', DEFAULT_JAR_PATH)
    sys.exit(1)

setup(name='scienceworld',
    version=VERSION,
    description='ScienceWorld: An interactive text environment to study AI agents on accomplishing tasks from the standardized elementary science curriculum.',
    author='Peter Jansen',
    packages=['scienceworld'],
    include_package_data=True,
    package_dir={'scienceworld': 'scienceworld'},
    package_data={'scienceworld': [JAR_FILE, OBJECTS_LUT_FILE]},
    url="https://scienceworld.github.io",
    long_description=open("README.md").read(),
    long_description_content_type="text/markdown",
    install_requires=open('requirements.txt').readlines(),
    extras_require={
        'webserver': open('requirements.txt').readlines() + ['pywebio'],
    },
)
