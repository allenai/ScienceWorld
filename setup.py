import os.path, sys

from setuptools import setup

with open(os.path.join("scienceworld", "version.py")) as f:
    VERSION = f.readlines()[0].split("=")[-1].strip("' \n")

BASEPATH = os.path.dirname(os.path.abspath(__file__))
JAR_FILE = 'scienceworld-{version}.jar'.format(version=VERSION)
JAR_PATH = os.path.join(BASEPATH, 'scienceworld', JAR_FILE)
OBJECTS_LUT_FILE = "object_type_ids.tsv"

if not os.path.isfile(JAR_PATH):
    print('ERROR: Unable to find required library:', JAR_PATH)
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
