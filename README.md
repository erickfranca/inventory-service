# inventory-service — CI/CD & GitOps practice lab #2

A small Spring Boot REST API (in-memory CRUD for "items") that exists for one
purpose: **you** wire the CI/CD + GitOps + observability around it, from
scratch, without a pre-built solution to copy.

Stack in this repo (checked against official docs/release pages, July 2026):

| Component | Version used here | Why |
|---|---|---|
| Java | 25 (LTS) | Current LTS since Sept 2025 |
| Spring Boot | 4.1.0 | Latest stable (Jun 2026), needs Java 17+ |
| Build tool | Maven | matches your previous lab |

Run it locally to confirm it works before touching CI/CD:

```bash
mvn spring-boot:run
curl localhost:8080/actuator/health
curl -X POST localhost:8080/api/items -H "Content-Type: application/json" -d '{"name":"widget","quantity":10}'
curl localhost:8080/api/items
```

Actuator already exposes `health`, `info`, `metrics`, and `prometheus` (via
`spring-boot-starter-actuator` — you'll need to add
`micrometer-registry-prometheus` yourself when you get to the observability
stage).

---

## Your exercise — build these yourself, in order

Don't copy your previous lab's YAML wholesale. Open the official docs for
each tool, check you're using current syntax/versions, and write it from
memory + docs. Come back and ask me to review/debug once you've made an
attempt — that's where you'll actually learn.

### Stage 1 — Containerize
- [ ] Multi-stage `Dockerfile` (build with Maven, run on a slim JRE image)
- [ ] `.dockerignore`
- [ ] Confirm the image runs locally and `/actuator/health` responds
- Docs: https://docs.docker.com/build/building/multi-stage/

### Stage 2 — CI (GitHub Actions)
- [ ] Workflow that builds + tests on every push
- [ ] Builds and pushes the image to a registry (Docker Hub or GHCR — try
      GHCR this time, it avoids a second set of credentials since it uses
      `GITHUB_TOKEN`)
- [ ] Tags the image with the commit SHA, not just `latest`
- Docs (verify current major versions before pinning):
  - https://docs.github.com/actions
  - https://github.com/actions/checkout (currently on major v6)
  - https://github.com/actions/setup-java (currently on major v5 — use
    `distribution: temurin`)
  - https://github.com/docker/build-push-action

### Stage 3 — GitOps repo + Helm chart
- [ ] Separate `manifests-repo` with a Helm chart (`Chart.yaml`,
      `values.yaml`, `templates/deployment.yaml`, `templates/service.yaml`)
- [ ] Deployment references `image.repository` + `image.tag` from values
- [ ] Wire liveness/readiness probes to `/actuator/health/liveness` and
      `/actuator/health/readiness` (Spring Boot exposes these natively when
      `management.endpoint.health.probes.enabled=true`, already set in this
      repo's `application.yml`)
- [ ] `helm lint` and `helm template` pass locally
- Docs: https://helm.sh/docs/chart_template_guide/

### Stage 4 — CD step: update the manifest
- [ ] CI job (or separate workflow) checks out `manifests-repo` and bumps
      `image.tag` after a successful build, commits, pushes
- [ ] Decide: PAT vs. a GitHub App vs. Actions' built-in token — think
      through why `GITHUB_TOKEN` alone won't reach a second repo
- Docs: https://docs.github.com/actions/security-guides/automatic-token-authentication

### Stage 5 — ArgoCD
- [ ] Local cluster (kind or minikube)
- [ ] Install ArgoCD, create an `Application` pointing at `manifests-repo`
- [ ] Confirm auto-sync + self-heal actually work (edit a replica count by
      hand with `kubectl` and watch ArgoCD revert it)
- Docs: https://argo-cd.readthedocs.io/en/stable/getting_started/

### Stage 6 — Observability (new territory vs. your last lab)
- [ ] Add `micrometer-registry-prometheus` to `pom.xml`
- [ ] Install kube-prometheus-stack (Prometheus + Grafana) via Helm
- [ ] Confirm Prometheus is scraping `/actuator/prometheus`
- [ ] Import or build one Grafana dashboard for this service
- [ ] Optional: add OpenTelemetry auto-instrumentation for traces (Spring
      Boot 4 / Framework 7 has native OTel support for trace propagation —
      worth reading up on before wiring collectors manually)
- Docs:
  - https://prometheus.io/docs/prometheus/latest/getting_started/
  - https://github.com/prometheus-community/helm-charts
  - https://opentelemetry.io/docs/languages/java/

---

## Ground rules for using me while you work through this

- Bring me your YAML/config when something breaks — I'll help you debug
  and explain *why*, not hand you a fixed file straight away.
- Ask me to check version numbers against official docs before you pin
  anything long-term; these move fast.
- When you finish a stage, tell me and I'll review what you built.
