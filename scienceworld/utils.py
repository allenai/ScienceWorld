import re
import logging
import traceback

from scienceworld.constants import NAME2ID, ID2TASK


def infer_task(name_or_id):
    if name_or_id in NAME2ID:
        name_or_id = NAME2ID[name_or_id]

    if name_or_id in ID2TASK:
        name_or_id = ID2TASK[name_or_id]

    # Correct typo fixed in b807f742050ba5d9e0c5483624c39834368cd34f
    name_or_id = name_or_id.replace("mendellian", "mendelian")

    # Remove prefix "task-##-" and any parentheses from task name.
    name_or_id = re.sub(r"task-(\d|a|b)+-|[()]", "", name_or_id)

    return name_or_id


def deprecated_api_warning(logger, pending=True, camel_case=True):

    if pending:
        depstatus = "This feature will be deprecated soon."
    else:
        depstatus = "This feature is deprecated."

    if camel_case:
        message = f"You are using the camel case naming convention for the"\
                    f"python API. {depstatus} Please use snake case instead."
    else:
        message = f"{depstatus}. Please migrate away from this feature, as it may lead to"\
                    "unexpected behavior."

    formatted_message = f"\033[91m {message}\033[00m\nStack Trace:\n"
    
    s = traceback.format_stack()
    for stack_el in s[:-2]:
        formatted_message += stack_el

    logger.warning(formatted_message)
