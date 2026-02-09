<?php
namespace Core;

class Router {
    private $routes = [];

    public function add($method, $path, $handler, $middleware = null) {
        $this->routes[] = [
            'method' => $method,
            'path' => $path,
            'handler' => $handler,
            'middleware' => $middleware
        ];
    }

    public function dispatch() {
        $method = $_SERVER['REQUEST_METHOD'];
        $uri = parse_url($_SERVER['REQUEST_URI'], PHP_URL_PATH);
        
        // Remove 'api/public/index.php' or base folder from URI if present
        $uri = str_replace('/api/public', '', $uri);

        foreach ($this->routes as $route) {
            $pattern = str_replace('/', '\/', $route['path']);
            $pattern = preg_replace('/\{(\w+)\}/', '(?P<$1>[^/]+)', $pattern);
            $pattern = '/^' . $pattern . '/'; // Match prefix for potential sub-resources

            if ($route['method'] === $method && preg_match($pattern, $uri, $matches)) {
                // Execute Middleware
                if ($route['middleware']) {
                    $middleware = $route['middleware'];
                    if (is_array($middleware)) {
                        $class = $middleware[0];
                        $roles = $middleware[1] ?? [];
                        $class::check($roles);
                    } else {
                        $middleware::check();
                    }
                }

                $handler = explode('@', $route['handler']);
                $controllerName = "Controllers\\" . $handler[0];
                $action = $handler[1];

                if (class_exists($controllerName)) {
                    $controller = new $controllerName();
                    $params = array_filter($matches, 'is_string', ARRAY_FILTER_USE_KEY);
                    call_user_func_array([$controller, $action], $params);
                    return;
                }
            }
        }

        http_response_code(404);
        echo json_encode(['message' => 'Route not found', 'uri' => $uri]);
    }
}
