# ===== Stage 1 : Build =====
# Angular 21 nécessite Node 20.19+ / 22.12+ / 24 — on prend Node 22 pour être large
FROM node:22-alpine AS build
WORKDIR /app

# Cache des dépendances npm
COPY package*.json ./
RUN npm config set fetch-retries 5 && \
    npm config set fetch-retry-mintimeout 20000 && \
    npm config set fetch-retry-maxtimeout 120000 && \
    npm ci

# Code source
COPY . .

# ng build utilise la configuration "production" par défaut sur les projets
# Angular CLI modernes (17+), donc pas besoin de --configuration production
RUN npm run build

# ===== Stage 2 : Runtime (nginx) =====
FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf

# Angular 17+ (esbuild/application builder) produit dist/<projet>/browser/
# On copie tout dist/ puis on détecte automatiquement le bon sous-dossier
COPY --from=build /app/dist /usr/share/nginx/html/_dist

RUN BROWSER_DIR=$(find /usr/share/nginx/html/_dist -type d -name browser | head -n1) && \
    if [ -n "$BROWSER_DIR" ]; then \
        cp -r "$BROWSER_DIR"/* /usr/share/nginx/html/; \
    else \
        PROJECT_DIR=$(find /usr/share/nginx/html/_dist -mindepth 1 -maxdepth 1 -type d | head -n1) && \
        cp -r "$PROJECT_DIR"/* /usr/share/nginx/html/; \
    fi && \
    rm -rf /usr/share/nginx/html/_dist

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
