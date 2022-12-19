import re

from scienceworld.constants import NAME2ID, ID2TASK


def infer_task(name_or_id):
    if name_or_id in NAME2ID:
        name_or_id = NAME2ID[name_or_id]

    if name_or_id in ID2TASK:
        name_or_id = ID2TASK[name_or_id]

    # Remove prefix "task-##-" from task name.
    name_or_id = re.sub(r"task-(\d|a|b)+-", "", name_or_id)

    return name_or_id
