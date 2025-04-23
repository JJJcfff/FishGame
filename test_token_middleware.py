#!/usr/bin/env python3
import requests
import time
import hashlib
import argparse

def generate_token_locally(secret, timestamp):
    """
    Generate token using the same algorithm as the Java service
    """
    data = f"{secret}:{timestamp}"
    hash_obj = hashlib.sha256(data.encode('utf-8'))
    return hash_obj.hexdigest()

def get_token_from_server(base_url, secret):
    """
    Get token from the server's generate-token endpoint
    """
    # Add current timestamp for the timestamp filter
    current_timestamp = str(int(time.time() * 1000))
    headers = {
        "FishGame-Request-Timestamp": current_timestamp
    }
    response = requests.get(f"{base_url}/user/generate-token", params={"secret": secret}, headers=headers)
    if response.status_code == 200:
        data = response.json()
        return data.get("token"), data.get("timestamp")
    else:
        print(f"Error getting token: {response.status_code}")
        print(response.text)
        return None, None

def test_endpoint_with_token(base_url, endpoint, token, timestamp):
    """
    Test an endpoint using the token and timestamp headers
    """
    headers = {
        "FishGame-Request-Token": token,
        "FishGame-Request-Token-Timestamp": timestamp,
        "FishGame-Request-Timestamp": timestamp  # Add the regular timestamp header for the timestamp filter
    }
    response = requests.get(f"{base_url}/{endpoint}", headers=headers)
    return response

def main():
    parser = argparse.ArgumentParser(description="Test FishGame's token verification middleware")
    parser.add_argument("--url", default="http://localhost:8080", help="Base URL of the FishGame server")
    parser.add_argument("--secret", default="123456789", help="Token secret (must match server configuration)")
    parser.add_argument("--endpoint", default="user/basic", help="Endpoint to test (without leading slash)")
    parser.add_argument("--userid", default="testuser", help="User ID parameter for endpoints that require it")
    parser.add_argument("--test-all", action="store_true", help="Run all test cases")
    
    args = parser.parse_args()
    
    print(f"Testing FishGame token middleware at {args.url}")
    
    # Test 1: Get token from server and use it
    print("\n--- Test 1: Get token from server and use it ---")
    token, timestamp = get_token_from_server(args.url, args.secret)
    if token and timestamp:
        print(f"Got token: {token[:10]}... timestamp: {timestamp}")
        
        response = test_endpoint_with_token(args.url, f"{args.endpoint}?userId={args.userid}", token, timestamp)
        print(f"Response status: {response.status_code}")
        print(f"Response body: {response.text}")
    else:
        print("Failed to get token from server")
        
    # Test 2: Generate token locally and use it
    if args.test_all:
        print("\n--- Test 2: Generate token locally and use it ---")
        timestamp = str(int(time.time() * 1000))
        token = generate_token_locally(args.secret, timestamp)
        print(f"Generated local token: {token[:10]}... timestamp: {timestamp}")
        
        response = test_endpoint_with_token(args.url, f"{args.endpoint}?userId={args.userid}", token, timestamp)
        print(f"Response status: {response.status_code}")
        print(f"Response body: {response.text}")
    
    # Test 3: Use invalid token
    if args.test_all:
        print("\n--- Test 3: Use invalid token ---")
        timestamp = str(int(time.time() * 1000))
        token = "invalidtoken"
        
        response = test_endpoint_with_token(args.url, f"{args.endpoint}?userId={args.userid}", token, timestamp)
        print(f"Response status: {response.status_code}")
        print(f"Response body: {response.text}")
    
    # Test 4: Use expired timestamp (if the server checks expiration)
    if args.test_all:
        print("\n--- Test 4: Use old timestamp ---")
        old_time = int(time.time() * 1000) - (60 * 60 * 1000)  # 1 hour ago
        timestamp = str(old_time)
        token = generate_token_locally(args.secret, timestamp)
        
        response = test_endpoint_with_token(args.url, f"{args.endpoint}?userId={args.userid}", token, timestamp)
        print(f"Response status: {response.status_code}")
        print(f"Response body: {response.text}")
    
    # Test 5: Use future timestamp
    if args.test_all:
        print("\n--- Test 5: Use future timestamp ---")
        future_time = int(time.time() * 1000) + (60 * 60 * 1000)  # 1 hour in the future
        timestamp = str(future_time)
        token = generate_token_locally(args.secret, timestamp)
        
        response = test_endpoint_with_token(args.url, f"{args.endpoint}?userId={args.userid}", token, timestamp)
        print(f"Response status: {response.status_code}")
        print(f"Response body: {response.text}")
    
if __name__ == "__main__":
    main() 