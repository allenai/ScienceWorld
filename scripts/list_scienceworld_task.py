from scienceworld import ScienceWorldEnv

env = ScienceWorldEnv()

task_ids = []
task_names = []
task_variations = []

for task_id, task in env.tasks.items():
    task_ids.append(str(task_id))
    task_names.append(task)
    task_variations.append(str(env.getMaxVariations(task)))


task_id_max_len = max(map(len, task_ids))
task_name_max_len = max(map(len, task_names))
task_variation_max_len = max(map(len, task_variations))

print("TASK LIST:")
for task_id, task_name, task_variation in zip(task_ids, task_names, task_variations):
    print(f"  {task_id.rjust(task_id_max_len)}"
          f"\t{task_name.rjust(task_name_max_len)}"
          f"\t({task_variation.rjust(task_variation_max_len)} variations)")
