import os.path, sys

from setuptools import setup


BASEPATH = os.path.dirname(os.path.abspath(__file__))
JAR_FILE = 'scienceworld-1.0.jar'
JAR_PATH = os.path.join(BASEPATH, 'scienceworld', JAR_FILE)
OBJECTS_LUT_FILE = "object_type_ids.tsv"

# TODO: build jar using package.sh?

if not os.path.isfile(JAR_PATH):
    print('ERROR: Unable to find required library:', JAR_PATH)
    sys.exit(1)

setup(name='scienceworld',
    version='1.0.0rc3',
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
