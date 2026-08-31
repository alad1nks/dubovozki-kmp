const baseUrl = process.env.DUBOVOZKI_PRODUCTION_DATABASE_URL;

if (!baseUrl?.startsWith("https://")) {
  throw new Error("DUBOVOZKI_PRODUCTION_DATABASE_URL must be an HTTPS URL");
}

const checks = [
  ["busSchedule", (value) => Array.isArray(value?.busList)],
  ["services", (value) => value !== null && typeof value === "object"],
  ["servicesSchedule/linenRoom", (value) =>
    ["firstBuilding", "secondBuilding", "thirdBuilding"].every((key) => Array.isArray(value?.[key]))],
];

for (const [path, matchesSchema] of checks) {
  const response = await fetch(`${baseUrl.replace(/\/$/, "")}/${path}.json`);
  if (!response.ok) throw new Error(`${path}: HTTP ${response.status}`);
  const value = await response.json();
  if (!matchesSchema(value)) throw new Error(`${path}: unexpected public schema`);
  console.log(`${path}: available and schema-compatible`);
}
