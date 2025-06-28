#!/bin/bash

echo "🔐 JWT Secret Generator"
echo "======================"

# Generate a 64-character random string (512 bits)
JWT_SECRET=$(openssl rand -base64 48 | tr -d '\n' | head -c 64)

echo "Generated secure JWT secret (64 characters):"
echo "$JWT_SECRET"
echo ""

# Update .env file
if [ -f ".env" ]; then
    # Backup current .env
    cp .env .env.backup
    
    # Replace JWT_SECRET in .env
    sed -i "s/JWT_SECRET=.*/JWT_SECRET=$JWT_SECRET/" .env
    echo "✅ Updated .env file (backup created as .env.backup)"
else
    echo "⚠️  .env file not found. Please create it from .env.example first"
fi

echo ""
echo "🔒 Security Note:"
echo "   - This secret is 512 bits (64 characters)"
echo "   - Store it securely in production"
echo "   - Never commit it to version control"
echo "   - Rotate it periodically for better security"
