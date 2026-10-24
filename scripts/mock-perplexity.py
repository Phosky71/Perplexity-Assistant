#!/usr/bin/env python3
"""
Mock Perplexity API Server for local development and testing.

Runs on 127.0.0.1:8765 and responds to POST /chat/completions
with Perplexity-compatible JSON responses.

Environment variables:
    MOCK_MODE: ok | 401 | 429 | 500 | empty | nousage (default: ok)
    MOCK_DELAY: delay in seconds before responding (default: 0)

Usage:
    MOCK_MODE=429 MOCK_DELAY=2 python scripts/mock-perplexity.py
    python scripts/mock-perplexity.py  # defaults to ok mode, no delay
"""

import json
import os
import sys
import time
from http.server import HTTPServer, BaseHTTPRequestHandler
from typing import Dict, Any
from urllib.parse import urlparse


# Configuration from environment
MOCK_MODE = os.environ.get("MOCK_MODE", "ok").lower()
MOCK_DELAY = float(os.environ.get("MOCK_DELAY", "0"))

HOST = "127.0.0.1"
PORT = 8765
ENDPOINT = "/chat/completions"

# Predefined responses per MOCK_MODE
RESPONSES: Dict[str, Dict[str, Any]] = {
    "ok": {
        "status": 200,
        "body": {
            "choices": [
                {
                    "message": {
                        "role": "assistant",
                        "content": "## Mock Response\n\nThis is a **mock response** from the local Perplexity mock server.\n\n```python\n# Example code block\ndef hello():\n    print('Hello from mock!')\n```\n\n> Citations are included below."
                    }
                }
            ],
            "usage": {
                "prompt_tokens": 10,
                "completion_tokens": 5
            },
            "citations": [
                "https://example.com/source1",
                "https://example.com/source2"
            ]
        }
    },
    "401": {
        "status": 401,
        "body": {
            "error": {
                "message": "Unauthorized",
                "type": "auth_error",
                "code": "invalid_api_key"
            }
        }
    },
    "429": {
        "status": 429,
        "body": {
            "message": "Rate limit exceeded. Please try again later.",
            "type": "rate_limit_error",
            "code": "rate_limit_exceeded"
        }
    },
    "500": {
        "status": 500,
        "body": "Internal Server Error"
    },
    "empty": {
        "status": 200,
        "body": {
            "choices": []
        }
    },
    "nousage": {
        "status": 200,
        "body": {
            "choices": [
                {
                    "message": {
                        "role": "assistant",
                        "content": "OK response without usage object."
                    }
                }
            ]
        }
    }
}


class MockPerplexityHandler(BaseHTTPRequestHandler):
    """HTTP request handler for the mock Perplexity API."""

    def _send_response(self, status: int, body: Any) -> None:
        """Send HTTP response with JSON body (or plain text for 500)."""
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.end_headers()

        if isinstance(body, str):
            self.wfile.write(body.encode("utf-8"))
        else:
            self.wfile.write(json.dumps(body, ensure_ascii=False).encode("utf-8"))

    def do_POST(self) -> None:
        """Handle POST requests to /chat/completions."""
        parsed = urlparse(self.path)

        if parsed.path != ENDPOINT:
            self._send_response(404, {"error": "Not found", "path": parsed.path})
            return

        # Read request body (not used but consume it)
        content_length = int(self.headers.get("Content-Length", 0))
        if content_length > 0:
            self.rfile.read(content_length)

        # Apply delay if configured
        if MOCK_DELAY > 0:
            time.sleep(MOCK_DELAY)

        # Get response for current mode
        response = RESPONSES.get(MOCK_MODE, RESPONSES["ok"])
        self._send_response(response["status"], response["body"])

    def do_GET(self) -> None:
        """Handle GET requests (health check)."""
        if self.path == "/health":
            self._send_response(200, {"status": "ok", "mode": MOCK_MODE})
        else:
            self._send_response(404, {"error": "Not found"})

    def log_message(self, format: str, *args: Any) -> None:
        """Override to customize log format."""
        sys.stderr.write(f"[{self.log_date_time_string()}] {format % args}\n")


def main() -> None:
    """Start the mock server."""
    if MOCK_MODE not in RESPONSES:
        print(f"Warning: Unknown MOCK_MODE='{MOCK_MODE}', defaulting to 'ok'")
        # Will fall back to 'ok' via dict.get()

    server = HTTPServer((HOST, PORT), MockPerplexityHandler)
    print(f"Mock Perplexity API running on http://{HOST}:{PORT}{ENDPOINT}")
    print(f"MOCK_MODE={MOCK_MODE}, MOCK_DELAY={MOCK_DELAY}s")
    print("Press Ctrl+C to stop")

    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nShutting down...")
        server.shutdown()


if __name__ == "__main__":
    main()