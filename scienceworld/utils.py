import re
import warnings

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


def deprecated_api_warning(pending=True, camel_case=True):
    if pending:
        depstatus = "This feature will be deprecated soon."
    else:
        depstatus = "This feature is deprecated."

    if camel_case:
        message = f"You are using the camel case naming convention for the \
                python API. {depstatus} Please use snake case instead."
    else:
        message = f"{depstatus}. Please do not use it, as it may lead to \
                unexpected behavior."

    if pending:
        category = PendingDeprecationWarning
    else:
        category = DeprecationWarning


    warnings.warn(message, category, stacklevel=2)
